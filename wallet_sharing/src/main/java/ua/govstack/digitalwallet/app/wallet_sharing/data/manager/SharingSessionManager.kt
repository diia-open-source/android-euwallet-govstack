package ua.govstack.digitalwallet.app.wallet_sharing.data.manager

import androidx.activity.ComponentActivity
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocument
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocuments
import eu.europa.ec.eudi.iso18013.transfer.response.RequestProcessor
import eu.europa.ec.eudi.iso18013.transfer.toKotlinResult
import eu.europa.ec.eudi.wallet.EudiWallet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.SharingSessionConfig
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.toUri
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.CheckKeyUnlockResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ResponseReceivedResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.SendRequestedDocumentsResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.TransferEventResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.WalletCoreResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.EudiWalletListenerWrapper
import java.net.URI

/**
 * Common scoped interactor that has all the complexity and required interaction with the EudiWallet Core.
 * */
interface SharingSessionManager {
    /**
     * On the initialization it adds the core listener and remove it when scope is canceled.
     * When the scope is canceled so does the presentation
     *
     * @return Hot Flow that emits the Core's status callback.
     * */
    val events: SharedFlow<TransferEventResult>

    /**
     * User selection data for request step
     * */
    val disclosedDocuments: MutableList<DisclosedDocument>?

    /**
     * Verifier name so it can be retrieving across screens
     * */
    val verifierName: String?

    val verifierIsTrusted: Boolean?

    val redirectUri: URI?

    /**
     * Set [SharingSessionConfig]
     * */
    fun setConfig(config: SharingSessionConfig)

    /**
     * Terminates the presentation and kills the coroutine scope that [events] live in
     * */
    fun stopPresentation()

    /**
     * Starts QR engagement. This will trigger [events] emission.
     *
     * [TransferEventResult.QrEngagementReady] -> QR String to show QR
     *
     * [TransferEventResult.Connecting] -> Connecting
     *
     * [TransferEventResult.Connected] -> Connected. We can proceed to the next screen
     * */
    fun startQrEngagement()

    /**
     * Enable/Disable NFC service
     * */
    fun toggleNfcEngagement(componentActivity: ComponentActivity, toggle: Boolean)

    /**
     * Transform UI models to Domain and create -> sent the request.
     *
     * @return Flow that emits the creation state. On Success send the request.
     * The response of that request is emitted through [events]
     *  */
    fun checkForKeyUnlock(): Flow<CheckKeyUnlockResult>

    fun sendRequestedDocuments(): SendRequestedDocumentsResult

    /**
     * Updates the UI model
     * @param disclosedDocuments User updated data through UI Events
     * */
    fun updateRequestedDocuments(disclosedDocuments: MutableList<DisclosedDocument>?)

    /**
     * @return flow that maps the state from [events] emission to what we consider as success state
     * */
    fun mappedCallbackStateFlow(): Flow<ResponseReceivedResult>

    /**
     * The main observation point for collecting state for the Request flow.
     * Exposes a single flow for two operations([checkForKeyUnlock] - [mappedCallbackStateFlow])
     * and a single state
     * @return flow that emits create, sent, receive states
     * */
    fun observeSentDocumentsRequest(): Flow<WalletCoreResult>
}


internal class SharingSessionManagerImpl(
    private val eudiWallet: EudiWallet,
    private val resourceProvider: ResourceProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SharingSessionManager {

    private val genericErrorMessage = resourceProvider.genericErrorMessage()

    private val coroutineScope = CoroutineScope(dispatcher + SupervisorJob())

    private lateinit var _config: SharingSessionConfig

    override var disclosedDocuments: MutableList<DisclosedDocument>? = null

    private var processedRequest: RequestProcessor.ProcessedRequest.Success? = null

    override var verifierName: String? = null

    override var verifierIsTrusted: Boolean? = null

    override var redirectUri: URI? = null

    override fun setConfig(config: SharingSessionConfig) {
        _config = config
    }

    override val events: SharedFlow<TransferEventResult> = callbackFlow {
        val eventListenerWrapper = EudiWalletListenerWrapper(
            onQrEngagementReady = { qrCode ->
                trySendBlocking(
                    TransferEventResult.QrEngagementReady(qrCode = qrCode)
                )
            },
            onConnected = {
                trySendBlocking(
                    TransferEventResult.Connected
                )
            },
            onConnecting = {
                trySendBlocking(
                    TransferEventResult.Connecting
                )
            },
            onDisconnected = {
                trySendBlocking(
                    TransferEventResult.Disconnected
                )
            },
            onError = { errorMessage ->
                trySendBlocking(
                    TransferEventResult.Error(
                        error = errorMessage.ifEmpty { genericErrorMessage }
                    )
                )
            },
            onRequestReceived = { requestedDocumentData ->
                trySendBlocking(
                    requestedDocumentData.getOrNull()?.let { requestedDocuments ->

                        processedRequest = requestedDocuments

                        verifierName = requestedDocuments.requestedDocuments
                            .firstOrNull()?.readerAuth?.readerCommonName

                        val firstReaderAuth = requestedDocuments.requestedDocuments
                            .firstOrNull()?.readerAuth

                        val isTrusted = firstReaderAuth?.readerCertificatedIsTrusted == true
                        verifierIsTrusted = isTrusted

                        TransferEventResult.RequestReceived(
                            requestData = requestedDocuments.requestedDocuments,
                            verifierName = verifierName,
                            verifierIsTrusted = isTrusted
                        )
                    } ?: TransferEventResult.Error(error = genericErrorMessage)
                )
            },
            onResponseSent = {
                trySendBlocking(
                    TransferEventResult.ResponseSent
                )
            },
            onRedirect = { uri ->
                redirectUri = uri

                trySendBlocking(
                    TransferEventResult.Redirect(
                        uri = uri
                    )
                )
            },

            intentToSend = {
                trySendBlocking(
                    TransferEventResult.IntentToSend
                )
            }
        )

        addListener(eventListenerWrapper)
        awaitClose {
            removeListener(eventListenerWrapper)
            eudiWallet.stopProximityPresentation()
        }
    }.safeAsync {
        TransferEventResult.Error(
            error = it.localizedMessage ?: resourceProvider.genericErrorMessage()
        )
    }.shareIn(coroutineScope, SharingStarted.Lazily, 2)

    override fun startQrEngagement() {
        eudiWallet.startProximityPresentation()
    }

    override fun toggleNfcEngagement(componentActivity: ComponentActivity, toggle: Boolean) {
        try {
            if (toggle) {
                eudiWallet.enableNFCEngagement(componentActivity)
            } else {
                eudiWallet.disableNFCEngagement(componentActivity)
            }
        } catch (_: Exception) {
        }
    }

    override fun checkForKeyUnlock() = flow {
        disclosedDocuments?.let {
            if (eudiWallet.config.userAuthenticationRequired) {
                //no-op
            } else {
                emit(
                    CheckKeyUnlockResult.RequestIsReadyToBeSent
                )
            }
        }
    }.safeAsync {
        CheckKeyUnlockResult.Failure(
            error = it.localizedMessage ?: genericErrorMessage
        )
    }

    override fun sendRequestedDocuments(): SendRequestedDocumentsResult {
        return disclosedDocuments?.let { safeDisclosedDocuments ->

            var result: SendRequestedDocumentsResult =
                SendRequestedDocumentsResult.RequestSent

            processedRequest?.generateResponse(DisclosedDocuments(safeDisclosedDocuments.toList()))
                ?.toKotlinResult()
                ?.onFailure {
                    val errorMessage = it.localizedMessage ?: genericErrorMessage
                    result = SendRequestedDocumentsResult.Failure(
                        error = errorMessage
                    )
                }
                ?.onSuccess {
                    eudiWallet.sendResponse(it.response)
                    result = SendRequestedDocumentsResult.RequestSent
                }
            result
        } ?: SendRequestedDocumentsResult.Failure(
            error = genericErrorMessage
        )
    }

    override fun mappedCallbackStateFlow(): Flow<ResponseReceivedResult> {
        return events.mapNotNull { response ->
            when (response) {

                // Fix: Wallet core should return Success state here
                is TransferEventResult.Error -> {
                    if (response.error == "Peer disconnected without proper session termination") {
                        ResponseReceivedResult.Success
                    } else {
                        ResponseReceivedResult.Failure(error = response.error)
                    }
                }

                is TransferEventResult.Redirect -> {
                    ResponseReceivedResult.Redirect(uri = response.uri)
                }

                is TransferEventResult.Disconnected -> {
                    when {
                        events.replayCache.firstOrNull() is TransferEventResult.Redirect -> null
                        else -> ResponseReceivedResult.Success
                    }
                }

                is TransferEventResult.ResponseSent -> ResponseReceivedResult.Success

                else -> null
            }
        }.safeAsync {
            ResponseReceivedResult.Failure(
                error = it.localizedMessage ?: genericErrorMessage
            )
        }
    }

    override fun observeSentDocumentsRequest(): Flow<WalletCoreResult> =
        merge(checkForKeyUnlock(), mappedCallbackStateFlow()).mapNotNull {
            when (it) {
                is CheckKeyUnlockResult.Failure -> {
                    WalletCoreResult.Failure(it.error)
                }

                is ResponseReceivedResult.Failure -> {
                    WalletCoreResult.Failure(it.error)
                }

                is ResponseReceivedResult.Redirect -> {
                    WalletCoreResult.Redirect(
                        uri = it.uri
                    )
                }

                is CheckKeyUnlockResult.RequestIsReadyToBeSent -> {
                    WalletCoreResult.RequestIsReadyToBeSent
                }

                else -> {
                    WalletCoreResult.Success
                }
            }
        }.safeAsync {
            WalletCoreResult.Failure(
                error = it.localizedMessage ?: genericErrorMessage
            )
        }

    override fun updateRequestedDocuments(disclosedDocuments: MutableList<DisclosedDocument>?) {
        this.disclosedDocuments = disclosedDocuments
    }

    override fun stopPresentation() {
        coroutineScope.cancel()
        CoroutineScope(dispatcher).launch {
            eudiWallet.stopProximityPresentation()
            eudiWallet.stopRemotePresentation()
        }
    }

    private fun addListener(listener: EudiWalletListenerWrapper) {
        val config = requireInit { _config }
        eudiWallet.addTransferEventListener(listener)
        if (config is SharingSessionConfig.OpenId4VP) {
            eudiWallet.startRemotePresentation(config.uri.toUri())
        }
    }

    private fun removeListener(listener: EudiWalletListenerWrapper) {
        requireInit { _config }
        eudiWallet.removeTransferEventListener(listener)
    }

    private fun <T> requireInit(block: () -> T): T {
        if (!::_config.isInitialized) {
            throw IllegalStateException("setConfig() must be called before using the SharingSessionManager")
        }
        return block()
    }

}
