package ua.govstack.digitalwallet.app.wallet_issuing.data.repository

import androidx.work.WorkManager
import eu.europa.ec.eudi.openid4vci.TxCodeInputMode
import eu.europa.ec.eudi.wallet.document.DocumentId
import eu.europa.ec.eudi.wallet.issue.openid4vci.Offer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.model.FetchScopedDocumentsResult
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ScopedDocumentDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.toDocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.issuance.DocumentIssuanceManager
import ua.govstack.digitalwallet.app.wallet_core.issuance.deferred.DeferredIssuingWork
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.DeferredDocumentDataDomain
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssuanceMethod
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssueDeferredDocumentResult
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssueDocumentsResult
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.ResolveDocumentOfferResult
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.safeLet
import ua.govstack.digitalwallet.app.wallet_issuing.data.transformer.documentIdentifier
import ua.govstack.digitalwallet.app.wallet_issuing.data.transformer.getIssuerLogo
import ua.govstack.digitalwallet.app.wallet_issuing.data.transformer.getIssuerName
import java.net.URI
import java.util.Locale

sealed class IssueDocumentsRepositoryResult {
    data class Success(val documentIds: List<DocumentId>) : IssueDocumentsRepositoryResult()
    data object DeferredSuccess : IssueDocumentsRepositoryResult()
    data class Failure(val errorMessage: String) : IssueDocumentsRepositoryResult()
}

sealed class IssueDocumentOfferRepositoryResult {
    data class Success(val documentIds: List<DocumentId>) : IssueDocumentOfferRepositoryResult()
    data object DeferredSuccess : IssueDocumentOfferRepositoryResult()
    data class Failure(val errorMessage: String) : IssueDocumentOfferRepositoryResult()
    data class AlreadyExists(val type: DocumentIdentifier) : IssueDocumentOfferRepositoryResult()
}

sealed class ResolveDocumentOfferRepositoryResult {
    data class Success(val offer: Offer) : ResolveDocumentOfferRepositoryResult()
    data class UntrustedIssuer(val issuerId: String?) : ResolveDocumentOfferRepositoryResult()
    data class Failure(val errorMessage: String) : ResolveDocumentOfferRepositoryResult()
    data class NoDocument(
        val issuerName: String,
        val issuerLogo: URI?,
    ) : ResolveDocumentOfferRepositoryResult()

    data object PIDRequired : ResolveDocumentOfferRepositoryResult()
}

sealed class IssueDeferredDocumentRepositoryResult {
    data class Issued(val deferredDocumentData: DeferredDocumentDataDomain) :
        IssueDeferredDocumentRepositoryResult()
    data class NotReady(val deferredDocumentData: DeferredDocumentDataDomain) :
        IssueDeferredDocumentRepositoryResult()
    data class Failed(val documentId: DocumentId, val errorMessage: String) :
        IssueDeferredDocumentRepositoryResult()
    data class Expired(val documentId: DocumentId) : IssueDeferredDocumentRepositoryResult()
}

sealed class GetAddDocumentOptionsRepositoryResult {
    data class Success(val options: List<ScopedDocumentDomain>) :
        GetAddDocumentOptionsRepositoryResult()

    data class NoOptions(val errorMsg: String) : GetAddDocumentOptionsRepositoryResult()
    data class Failure(val error: String) : GetAddDocumentOptionsRepositoryResult()
}

interface IssuingRepository {
    fun getAddDocumentOption(): Flow<GetAddDocumentOptionsRepositoryResult>

    fun issueDocuments(
        issuanceMethod: IssuanceMethod,
        configIds: List<String>,
        issuerId: String
    ): Flow<IssueDocumentsRepositoryResult>

    fun issueDocumentsByOffer(
        offerUri: String,
        txCode: String? = null
    ): Flow<IssueDocumentOfferRepositoryResult>

    fun resolveDocumentOffer(offerUri: String): Flow<ResolveDocumentOfferRepositoryResult>

    fun issueDeferredDocument(docId: DocumentId): Flow<IssueDeferredDocumentRepositoryResult>

    fun resumeOpenId4VciWithAuthorization(uri: String)
}

internal class IssuingRepositoryImpl(
    private val documentStore: DocumentStore,
    private val documentIssuanceManager: DocumentIssuanceManager,
    private val dispatcherProvider: DispatcherProvider,
    private val workManager: WorkManager
) : IssuingRepository {

    val credentialOffers: MutableMap<String, Offer> = mutableMapOf()

    override fun getAddDocumentOption(): Flow<GetAddDocumentOptionsRepositoryResult> =
        flow {
            val state =
                documentIssuanceManager.getScopedDocuments(Locale.getDefault())

            when (state) {
                is FetchScopedDocumentsResult.Failure -> {
                    emit(GetAddDocumentOptionsRepositoryResult.Failure(state.errorMessage))
                }

                is FetchScopedDocumentsResult.Success -> {
                    emit(processSuccessState(state.documents))
                }
            }
        }.safeAsync {
            GetAddDocumentOptionsRepositoryResult.Failure(
                error = it.message.orEmpty()
            )
        }.flowOn(dispatcherProvider.ioDispatcher())

    override fun issueDocuments(
        issuanceMethod: IssuanceMethod,
        configIds: List<String>,
        issuerId: String
    ): Flow<IssueDocumentsRepositoryResult> =
        documentIssuanceManager.issueDocuments(
            issuanceMethod = issuanceMethod,
            configIds = configIds,
            issuerId = issuerId
        ).mapToRepositoryIssueResult().flowOn(dispatcherProvider.ioDispatcher())

    override fun issueDocumentsByOffer(
        offerUri: String,
        txCode: String?
    ): Flow<IssueDocumentOfferRepositoryResult> {
        return flow {
            val issuedDocuments = documentStore.getAllIssuedDocuments()
            val requestedDocuments = credentialOffers[offerUri]?.offeredDocuments.orEmpty()
            val requestedIssuer = credentialOffers[offerUri]
                ?.credentialOffer
                ?.credentialIssuerIdentifier
                ?.toString()

            val alreadyExists = issuedDocuments.firstNotNullOfOrNull { issuedDocument ->
                issuedDocument.toDocumentIdentifier().takeIf { issuedType ->
                    issuedDocument.issuerMetadata?.credentialIssuerIdentifier == requestedIssuer &&
                            requestedDocuments.any { it.documentIdentifier == issuedType }
                }
            }

            if (alreadyExists != null) {
                emit(IssueDocumentOfferRepositoryResult.AlreadyExists(alreadyExists))
                return@flow
            }

            credentialOffers[offerUri]?.let { offer ->
                emitAll(
                    documentIssuanceManager.issueDocumentsByOffer(
                        offer = offer,
                        txCode = txCode
                    ).map { response ->
                        when (response) {
                            is IssueDocumentsResult.Success -> {
                                IssueDocumentOfferRepositoryResult.Success(documentIds = response.documentIds)
                            }
                            is IssueDocumentsResult.PartialSuccess -> {
                                IssueDocumentOfferRepositoryResult.Success(documentIds = response.documentIds)
                            }
                            is IssueDocumentsResult.Failure -> {
                                IssueDocumentOfferRepositoryResult.Failure(errorMessage = response.errorMessage)
                            }
                            is IssueDocumentsResult.DeferredSuccess -> {
                                DeferredIssuingWork.enqueue(workManager)
                                IssueDocumentOfferRepositoryResult.DeferredSuccess
                            }
                        }
                    })
            } ?: emit(
                IssueDocumentOfferRepositoryResult.Failure(errorMessage = NO_OFFER_ERROR)
            )
        }.safeAsync {
            IssueDocumentOfferRepositoryResult.Failure(
                errorMessage = it.message.orEmpty()
            )
        }
    }

    override fun resolveDocumentOffer(offerUri: String): Flow<ResolveDocumentOfferRepositoryResult> =
        documentIssuanceManager.resolveDocumentOffer(offerUri).map { state ->
            when (state) {
                is ResolveDocumentOfferResult.UntrustedIssuer -> {
                    ResolveDocumentOfferRepositoryResult.UntrustedIssuer(state.issuerId)
                }

                is ResolveDocumentOfferResult.Failure -> {
                    ResolveDocumentOfferRepositoryResult.Failure(state.errorMessage)
                }

                is ResolveDocumentOfferResult.Success -> {
                    credentialOffers[offerUri] = state.offer
                    val userLocale = Locale.getDefault()

                    val hasProperType =
                        state.offer.offeredDocuments.all { offeredDocument ->
                            val id = offeredDocument.documentIdentifier
                            id == DocumentIdentifier.MdocPid ||
                                    id == DocumentIdentifier.SdJwtPid ||
                                    id == DocumentIdentifier.MdocMdl
                        }

                    if (!hasProperType) {
                        return@map ResolveDocumentOfferRepositoryResult.Failure(INVALID_DOC_TYPE)
                    }

                    if (state.offer.offeredDocuments.isEmpty()) {
                        return@map ResolveDocumentOfferRepositoryResult.NoDocument(
                            issuerName = state.offer.getIssuerName(userLocale),
                            issuerLogo = state.offer.getIssuerLogo(userLocale),
                        )
                    }

                    val codeMinLength = 4
                    val codeMaxLength = 6

                    safeLet(
                        state.offer.txCodeSpec?.inputMode,
                        state.offer.txCodeSpec?.length
                    ) { inputMode, length ->

                        if ((length !in codeMinLength..codeMaxLength) ||
                            inputMode == TxCodeInputMode.TEXT
                        ) {
                            return@map ResolveDocumentOfferRepositoryResult.Failure(
                                INVALID_TRANSACTION_CODE_ERROR
                            )
                        }
                    }

                    val hasMainPid =
                        documentStore.getMainPidDocument() != null

                    val hasPidInOffer =
                        state.offer.offeredDocuments.any {
                            val id = it.documentIdentifier
                            id == DocumentIdentifier.MdocPid ||
                                    id == DocumentIdentifier.SdJwtPid
                        }

                    if (hasMainPid || hasPidInOffer) {

                        ResolveDocumentOfferRepositoryResult.Success(state.offer)
                    } else {
                        ResolveDocumentOfferRepositoryResult.PIDRequired
                    }
                }
            }
        }.flowOn(dispatcherProvider.ioDispatcher())

    override fun issueDeferredDocument(docId: DocumentId): Flow<IssueDeferredDocumentRepositoryResult> =
        documentIssuanceManager.issueDeferredDocument(docId).map { state ->
            when (state) {
                is IssueDeferredDocumentResult.Issued -> {
                    IssueDeferredDocumentRepositoryResult.Issued(state.deferredDocumentData)
                }

                is IssueDeferredDocumentResult.NotReady -> {
                    IssueDeferredDocumentRepositoryResult.NotReady(state.deferredDocumentData)
                }

                is IssueDeferredDocumentResult.Failed -> {
                    IssueDeferredDocumentRepositoryResult.Failed(
                        documentId = state.documentId,
                        errorMessage = state.errorMessage
                    )
                }

                is IssueDeferredDocumentResult.Expired -> {
                    IssueDeferredDocumentRepositoryResult.Expired(state.documentId)
                }
            }
        }.flowOn(dispatcherProvider.ioDispatcher())

    override fun resumeOpenId4VciWithAuthorization(uri: String) {
        documentIssuanceManager.resumeOpenId4VciWithAuthorization(uri)
    }

    companion object {
        private const val NO_OFFER_ERROR = "No offer by link"
        private const val INVALID_TRANSACTION_CODE_ERROR = "Invalid transaction code"
        private const val INVALID_DOC_TYPE = "Invalid doc type"
    }

    private fun Flow<IssueDocumentsResult>.mapToRepositoryIssueResult() = map { state ->
        when (state) {
            is IssueDocumentsResult.DeferredSuccess -> {
                DeferredIssuingWork.enqueue(workManager)
                IssueDocumentsRepositoryResult.DeferredSuccess
            }

            is IssueDocumentsResult.Failure -> {
                IssueDocumentsRepositoryResult.Failure(state.errorMessage)
            }

            is IssueDocumentsResult.PartialSuccess -> {
                IssueDocumentsRepositoryResult.Success(state.documentIds)
            }

            is IssueDocumentsResult.Success -> {
                IssueDocumentsRepositoryResult.Success(state.documentIds)
            }
        }
    }

    private fun processSuccessState(
        rawDocuments: List<ScopedDocumentDomain>,
    ): GetAddDocumentOptionsRepositoryResult {
        if (rawDocuments.isEmpty()) return GetAddDocumentOptionsRepositoryResult.NoOptions("No options")

        val issuedDocuments = documentStore.getAllIssuedDocuments()
        val supportedFormats =
            documentStore.getAllDocumentCategories().value.map { it.formatType }.toSet()

        val filteredDocuments = rawDocuments.filter { doc ->
            val isSupported = doc.formatType in supportedFormats
            val isNotYetIssued = issuedDocuments.none {
                it.toDocumentIdentifier() == doc.formatType?.toDocumentIdentifier() && it.issuerMetadata?.credentialIssuerIdentifier == doc.credentialIssuerId
            }

            isSupported && isNotYetIssued
        }

        return if (filteredDocuments.isEmpty()) {
            GetAddDocumentOptionsRepositoryResult.NoOptions("No Options")
        } else {
            GetAddDocumentOptionsRepositoryResult.Success(filteredDocuments)
        }
    }

}
