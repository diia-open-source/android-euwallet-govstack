package ua.govstack.digitalwallet.app.wallet_core.issuance

import androidx.core.net.toUri
import eu.europa.ec.eudi.openid4vci.CredentialIssuerMetadata
import eu.europa.ec.eudi.openid4vci.MsoMdocCredential
import eu.europa.ec.eudi.openid4vci.SdJwtVcCredential
import eu.europa.ec.eudi.wallet.EudiWallet
import eu.europa.ec.eudi.wallet.document.DeferredDocument
import eu.europa.ec.eudi.wallet.document.DocumentExtensions.getDefaultCreateDocumentSettings
import eu.europa.ec.eudi.wallet.document.DocumentId
import eu.europa.ec.eudi.wallet.issue.openid4vci.DeferredIssueResult
import eu.europa.ec.eudi.wallet.issue.openid4vci.IssueEvent
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import eu.europa.ec.eudi.wallet.issue.openid4vci.OfferResult
import eu.europa.ec.eudi.wallet.issue.openid4vci.OpenId4VciManager
import eu.europa.ec.eudi.wallet.issue.openid4vci.UntrustedIssuerException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.documentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.getLocalizedDisplayName
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.model.FetchScopedDocumentsResult
import ua.govstack.digitalwallet.app.wallet_core.documents.model.FormatType
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ScopedDocumentDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.toDocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.DeferredDocumentDataDomain
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssuanceMethod
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssueDeferredDocumentResult
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssueDocumentsResult
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.ResolveDocumentOfferResult
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import java.net.URLDecoder
import java.util.Locale
import java.util.concurrent.CancellationException

interface DocumentIssuanceManager {
    fun issueDocuments(
        issuanceMethod: IssuanceMethod,
        configIds: List<String>,
        issuerId: String,
        prioritizeDeferred: Boolean = false
    ): Flow<IssueDocumentsResult>

    fun issueDocumentsByOffer(
        offer: Offer,
        txCode: String? = null,
        prioritizeDeferred: Boolean = true
    ): Flow<IssueDocumentsResult>

    fun resolveDocumentOffer(offerUri: String): Flow<ResolveDocumentOfferResult>

    fun issueDeferredDocument(docId: DocumentId): Flow<IssueDeferredDocumentResult>

    fun resumeOpenId4VciWithAuthorization(uri: String)

    suspend fun getScopedDocuments(locale: Locale): FetchScopedDocumentsResult
}

data class VciConfig(
    val config: OpenId4VciManager.Config,
    val order: Int,
)

internal class DocumentIssuanceManagerImpl(
    private val eudiWallet: EudiWallet,
    private val walletCoreConfig: WalletCoreConfig,
    private val documentStore: DocumentStore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DocumentIssuanceManager {

    private val genericErrorMessage
        get() = "Something went wrong"

    private val documentErrorMessage
        get() = "Something went wrong issuing your document(s)"

    private var activeIssuerId: String? = null

    /**
     * A map of [OpenId4VciManager] instances, keyed by their [VciConfig].
     * This is initialized lazily, creating a manager for each configuration defined in
     * `[WalletCoreConfig.issuersConfig]`. This allows the repository to interact with multiple
     * credential issuers, each with its own specific configuration.
     */
    private val openId4VciManagers: Map<VciConfig, OpenId4VciManager> by lazy {
        walletCoreConfig.issuersConfig.associateWith { orderConfig ->
            eudiWallet.createOpenId4VciManager(config = orderConfig.config)
        }
    }

    override suspend fun getScopedDocuments(locale: Locale): FetchScopedDocumentsResult {
        return withContext(dispatcher) {
            runCatching {

                val metadata: Map<VciConfig, CredentialIssuerMetadata> =
                    openId4VciManagers.mapNotNull { (config, manager) ->
                        runCatching { manager.getIssuerMetadata().getOrThrow() }
                            .getOrNull()
                            ?.let { config to it }
                    }.toMap()

                val onlyPidAllowed = documentStore.getMainPidDocument() == null

                val documents: List<ScopedDocumentDomain> =
                    metadata.flatMap { (vciConfig, meta) ->
                        meta.credentialConfigurationsSupported.mapNotNull { (id, credentialConfig) ->

                            val name =
                                credentialConfig.credentialMetadata?.display?.getLocalizedDisplayName(
                                    userLocale = locale, fallback = id.value
                                ) ?: "Unrecognized name"

                            val isPid = when (credentialConfig) {
                                is MsoMdocCredential -> credentialConfig.docType.toDocumentIdentifier() == DocumentIdentifier.MdocPid
                                is SdJwtVcCredential -> credentialConfig.type.toDocumentIdentifier() == DocumentIdentifier.SdJwtPid
                                else -> false
                            }

                            val formatType = when (credentialConfig) {
                                is MsoMdocCredential -> credentialConfig.docType
                                is SdJwtVcCredential -> credentialConfig.type
                                else -> null
                            }
                            if (onlyPidAllowed && !isPid) {
                                null
                            } else {
                                ScopedDocumentDomain(
                                    name = name,
                                    configurationId = id.value,
                                    credentialIssuerId = vciConfig.config.issuerUrl,
                                    credentialIssuerOrder = vciConfig.order,
                                    formatType = formatType,
                                    isPid = isPid
                                )
                            }
                        }
                    }

                FetchScopedDocumentsResult.Success(documents = documents)
            }
        }.getOrElse {
            FetchScopedDocumentsResult.Failure(
                errorMessage = it.localizedMessage ?: genericErrorMessage
            )
        }
    }

    override fun issueDocuments(
        issuanceMethod: IssuanceMethod,
        configIds: List<String>,
        issuerId: String,
        prioritizeDeferred: Boolean
    ): Flow<IssueDocumentsResult> =
        issueDocumentsWithOpenId4VCI(
            configIds,
            issuerId,
            prioritizeDeferred
        ).mapNotNull { response ->
            if (issuanceMethod == IssuanceMethod.OPENID4VCI) {
                when (response) {
                    is IssueDocumentsResult.Failure ->
                        IssueDocumentsResult.Failure(
                            errorMessage = documentErrorMessage
                        )

                    is IssueDocumentsResult.Success ->
                        IssueDocumentsResult.Success(
                            response.documentIds
                        )

                    is IssueDocumentsResult.PartialSuccess ->
                        IssueDocumentsResult.Success(
                            response.documentIds
                        )

                    is IssueDocumentsResult.DeferredSuccess ->
                        IssueDocumentsResult.DeferredSuccess(
                            response.deferredDocuments
                        )
                }
            } else null
        }.safeAsync {
            IssueDocumentsResult.Failure(errorMessage = documentErrorMessage)
        }

    override fun issueDocumentsByOffer(
        offer: Offer,
        txCode: String?,
        prioritizeDeferred: Boolean
    ): Flow<IssueDocumentsResult> =
        callbackFlow {

            val issuerId = offer
                .credentialOffer
                .credentialIssuerIdentifier
                .toString()

            val manager: OpenId4VciManager? = openId4VciManagers.entries
                .find { (vciConfig, _) ->
                    vciConfig.config.issuerUrl == issuerId
                }?.value
                ?: openId4VciManagers.values.firstOrNull()

            require(manager != null) { documentErrorMessage }
            activeIssuerId = issuerId

            manager.issueDocumentByOffer(
                offer = offer,
                onIssueEvent = issuanceCallback(prioritizeDeferred),
                txCode = txCode,
            )
            awaitClose()
        }.safeAsync {
            IssueDocumentsResult.Failure(
                errorMessage = documentErrorMessage
            )
        }

    override fun resolveDocumentOffer(offerUri: String): Flow<ResolveDocumentOfferResult> =
        callbackFlow {

            val issuerId = extractCredentialIssuerFromOfferUri(offerUri).getOrNull()
            val matchedManager = issuerId?.let { id ->
                openId4VciManagers.entries.find { (vciConfig, _) ->
                    vciConfig.config.issuerUrl == id
                }?.value
            }
            val trustHandlingConfig = walletCoreConfig.trustHandlingConfig

            if (
                issuerId != null &&
                matchedManager == null &&
                trustHandlingConfig.enforceIssuerAllowList
            ) {
                trySendBlocking(ResolveDocumentOfferResult.UntrustedIssuer(issuerId))
                close()
                return@callbackFlow
            }

            val manager: OpenId4VciManager? = matchedManager ?: openId4VciManagers.values.firstOrNull()

            require(manager != null) { genericErrorMessage }

            manager.resolveDocumentOffer(offerUri) { result ->
                when (result) {
                    is OfferResult.Failure -> {
                        val cause = result.cause
                        trySendBlocking(
                            if (cause.isIssuerTrustFailure()) {
                                ResolveDocumentOfferResult.UntrustedIssuer(issuerId)
                            } else {
                                ResolveDocumentOfferResult.Failure(
                                    cause.localizedMessage ?: genericErrorMessage
                                )
                            }
                        )
                        close()
                    }

                    is OfferResult.Success -> {
                        trySendBlocking(
                            ResolveDocumentOfferResult.Success(
                                offer = result.offer
                            )
                        )
                        close()
                    }
                }
            }
            awaitClose()
        }.safeAsync {
            ResolveDocumentOfferResult.Failure(
                errorMessage = it.localizedMessage ?: genericErrorMessage
            )
        }

    override fun issueDeferredDocument(docId: DocumentId): Flow<IssueDeferredDocumentResult> =
        callbackFlow {
            (documentStore.getDocumentById(docId) as? DeferredDocument)?.let { deferredDoc ->

                val manager = deferredDoc.issuerMetadata?.credentialIssuerIdentifier
                    ?.let(openId4VciManagers::get)
                    ?: openId4VciManagers.values.firstOrNull()

                require(manager != null) { documentErrorMessage }

                manager.issueDeferredDocument(
                    deferredDocument = deferredDoc,
                    executor = null,
                    onIssueResult = { deferredIssuanceResult ->
                        when (deferredIssuanceResult) {
                            is DeferredIssueResult.DocumentFailed -> {
                                trySendBlocking(
                                    IssueDeferredDocumentResult.Failed(
                                        documentId = deferredIssuanceResult.documentId,
                                        errorMessage = deferredIssuanceResult.cause.localizedMessage
                                            ?: documentErrorMessage
                                    )
                                )
                            }

                            is DeferredIssueResult.DocumentIssued -> {
                                trySendBlocking(
                                    IssueDeferredDocumentResult.Issued(
                                        DeferredDocumentDataDomain(
                                            documentId = deferredIssuanceResult.documentId,
                                            formatType = deferredIssuanceResult.docType,
                                            docName = deferredIssuanceResult.name
                                        )
                                    )
                                )
                            }

                            is DeferredIssueResult.DocumentNotReady -> {
                                trySendBlocking(
                                    IssueDeferredDocumentResult.NotReady(
                                        DeferredDocumentDataDomain(
                                            documentId = deferredIssuanceResult.documentId,
                                            formatType = deferredIssuanceResult.docType,
                                            docName = deferredIssuanceResult.name
                                        )
                                    )
                                )
                            }

                            is DeferredIssueResult.DocumentExpired -> {
                                trySendBlocking(
                                    IssueDeferredDocumentResult.Expired(
                                        documentId = deferredIssuanceResult.documentId
                                    )
                                )
                            }
                        }
                    }
                )
            } ?: trySendBlocking(
                IssueDeferredDocumentResult.Failed(
                    documentId = docId,
                    errorMessage = documentErrorMessage
                )
            )

            awaitClose()
        }.safeAsync {
            IssueDeferredDocumentResult.Failed(
                documentId = docId,
                errorMessage = it.localizedMessage ?: genericErrorMessage
            )
        }

    override fun resumeOpenId4VciWithAuthorization(uri: String) {
        val issuerId = activeIssuerId ?: return

        val manager = openId4VciManagers.entries
            .find { (vciConfig, _) -> vciConfig.config.issuerUrl == issuerId }
            ?.value
            ?: return

        try {
            manager.resumeWithAuthorization(uri)
        } catch (_: Exception) {
        }
    }

    private fun issueDocumentsWithOpenId4VCI(
        configIds: List<String>,
        issuerId: String,
        prioritizeDeferred: Boolean
    ): Flow<IssueDocumentsResult> =
        callbackFlow {

            val manager: OpenId4VciManager? =
                openId4VciManagers.entries.find { (vciConfig, _) ->
                    vciConfig.config.issuerUrl == issuerId
                }?.value
            require(manager != null) { documentErrorMessage }
            activeIssuerId = issuerId

            manager.issueDocumentByConfigurationIdentifiers(
                credentialConfigurationIds = configIds,
                onIssueEvent = issuanceCallback(prioritizeDeferred)
            )

            awaitClose()

        }.safeAsync {
            IssueDocumentsResult.Failure(
                errorMessage = documentErrorMessage
            )
        }

    private fun ProducerScope<IssueDocumentsResult>.issuanceCallback(
        prioritizeDeferred: Boolean = true
    ): OpenId4VciManager.OnIssueEvent {

        var totalDocumentsToBeIssued = 0
        val nonIssuedDocuments: MutableMap<FormatType, String> = mutableMapOf()
        val deferredDocuments: MutableMap<DocumentId, FormatType> = mutableMapOf()
        val issuedDocuments: MutableMap<DocumentId, FormatType> = mutableMapOf()

        val listener = OpenId4VciManager.OnIssueEvent { event ->
            when (event) {
                is IssueEvent.DocumentFailed -> {
                    nonIssuedDocuments[event.docType] = event.name
                }

                is IssueEvent.DocumentRequiresCreateSettings -> {
                    launch {
                        val offeredDocIdentifier = event.offeredDocument.documentIdentifier

                        val documentIssuanceRule = walletCoreConfig
                            .documentIssuanceConfig
                            .getRuleForDocument(documentIdentifier = offeredDocIdentifier)

                        event.resume(
                            eudiWallet.getDefaultCreateDocumentSettings(
                                offeredDocument = event.offeredDocument,
                                credentialPolicy = documentIssuanceRule.policy,
                                numberOfCredentials = documentIssuanceRule.numberOfCredentials,
                            )
                        )
                    }
                }

                is IssueEvent.DocumentRequiresUserAuth -> {
                    /* no-op */
                }

                is IssueEvent.Failure -> {
                    if (event.cause.isAuthorizationCancelled()) {
                        return@OnIssueEvent
                    }
                    activeIssuerId = null
                    trySendBlocking(
                        IssueDocumentsResult.Failure(
                            errorMessage = documentErrorMessage
                        )
                    )
                }

                is IssueEvent.Finished -> {
                    activeIssuerId = null

                    if (deferredDocuments.isNotEmpty() && (prioritizeDeferred || (issuedDocuments.isEmpty()))) {
                        trySendBlocking(IssueDocumentsResult.DeferredSuccess(deferredDocuments))
                        return@OnIssueEvent
                    }

                    if (event.issuedDocuments.isEmpty()) {
                        trySendBlocking(
                            IssueDocumentsResult.Failure(
                                errorMessage = documentErrorMessage
                            )
                        )
                        return@OnIssueEvent
                    }

                    if (event.issuedDocuments.size == totalDocumentsToBeIssued) {
                        trySendBlocking(
                            IssueDocumentsResult.Success(
                                documentIds = event.issuedDocuments
                            )
                        )
                        return@OnIssueEvent
                    }

                    trySendBlocking(
                        IssueDocumentsResult.PartialSuccess(
                            documentIds = event.issuedDocuments,
                            nonIssuedDocuments = nonIssuedDocuments
                        )
                    )
                }

                is IssueEvent.DocumentIssued -> {
                    issuedDocuments[event.documentId] = event.docType
                }

                is IssueEvent.Started -> {
                    totalDocumentsToBeIssued = event.total
                }

                is IssueEvent.DocumentDeferred -> {
                    deferredDocuments[event.documentId] = event.docType
                }
            }
        }

        return listener
    }

    private fun extractCredentialIssuerFromOfferUri(offerUri: String): Result<String> =
        runCatching {
            val credentialOffer = offerUri.toUri().getQueryParameter("credential_offer")
            val decoded = URLDecoder.decode(credentialOffer, "UTF-8")
            val json = JSONObject(decoded)
            json.getString("credential_issuer")
        }

    private fun Throwable?.isIssuerTrustFailure(): Boolean {
        return this is UntrustedIssuerException
    }

    private fun Throwable.isAuthorizationCancelled(): Boolean {
        return generateSequence(this) { it.cause }
            .any { throwable ->
                throwable is CancellationException &&
                        throwable.message == "Authorization was cancelled"
            }
    }
}
