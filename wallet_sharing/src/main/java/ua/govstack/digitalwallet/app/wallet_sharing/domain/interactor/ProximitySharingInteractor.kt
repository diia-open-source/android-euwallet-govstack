package ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor

import android.util.Log
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocuments
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.wallet.configs.WalletCoreConfig
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.ProximityRepository
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingLoadingResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingRequestResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingSendResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingSuccessResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer.ProximitySharingTransformer

interface ProximitySharingInteractor {
    fun getRequestDocuments(): Flow<ProximitySharingRequestResult>
    fun updateRequestedDocuments(documents: DisclosedDocuments)
    fun observeResponse(): Flow<ProximitySharingLoadingResult>
    fun sendRequestedDocuments(): ProximitySharingSendResult
    fun getSharedDocuments(): Flow<ProximitySharingSuccessResult>
    fun stopPresentation()
}

internal class ProximitySharingInteractorImpl(
    private val resourceProvider: ResourceProvider,
    private val uuidProvider: UuidProvider,
    private val repository: ProximityRepository,
    private val walletCoreConfig: WalletCoreConfig,
) : ProximitySharingInteractor {

    private val genericErrorMsg
        get() = "Something went wrong"

    override fun getRequestDocuments(): Flow<ProximitySharingRequestResult> =
        repository.events
            .mapNotNull {
                ProximitySharingTransformer.mapRequestEvent(
                    response = it,
                    repository = repository,
                    resourceProvider = resourceProvider,
                    uuidProvider = uuidProvider,
                )
            }
            .mapNotNull { result ->
                when (result) {
                    is ProximitySharingRequestResult.Success -> {
                        if (
                            !walletCoreConfig.trustHandlingConfig.allowUntrustedVerifiers &&
                            !result.verifierIsTrusted
                        ) {
                            ProximitySharingRequestResult.UntrustedVerifier(result.verifierName)
                        } else {
                            result
                        }
                    }

                    is ProximitySharingRequestResult.NoData -> {
                        if (
                            !walletCoreConfig.trustHandlingConfig.allowUntrustedVerifiers &&
                            !result.verifierIsTrusted
                        ) {
                            ProximitySharingRequestResult.UntrustedVerifier(result.verifierName)
                        } else {
                            result
                        }
                    }

                    else -> result
                }
            }
            .safeAsync {
                ProximitySharingRequestResult.Failure(
                    error = it.localizedMessage ?: genericErrorMsg
                )
            }
            .onEach { response ->
                when (response) {
                    ProximitySharingRequestResult.Disconnect ->
                        Log.d(TAG, "Proximity: Disconnect")

                    is ProximitySharingRequestResult.Failure ->
                        Log.d(
                            TAG,
                            "Proximity: Fail to handle request translations from json ${response.error}"
                        )

                    is ProximitySharingRequestResult.NoData ->
                        Log.d(TAG, "Proximity: No data to share")

                    is ProximitySharingRequestResult.Success ->
                        Log.d(TAG, "Proximity: InteractorResult Success")

                    is ProximitySharingRequestResult.UntrustedVerifier ->
                        Log.d(TAG, "Proximity: Untrusted verifier ${response.verifierName}")
                }
            }

    override fun updateRequestedDocuments(documents: DisclosedDocuments) {
        repository.updateRequestedDocuments(documents)
    }

    override fun observeResponse(): Flow<ProximitySharingLoadingResult> =
        repository.observeSentDocumentsRequest()
            .mapNotNull { ProximitySharingTransformer.mapObserveResponse(it) }
            .onEach { state ->
                when (state) {
                    is ProximitySharingLoadingResult.Failure ->
                        Log.d(TAG, "Proximity: Fail to handle sharing request: ${state.error}")

                    ProximitySharingLoadingResult.RequestReadyToBeSent ->
                        Log.d(TAG, "Proximity: Requested documents ready to be sent")

                    ProximitySharingLoadingResult.Success ->
                        Log.d(TAG, "Proximity: Response successfully sent")
                }
            }

    override fun sendRequestedDocuments(): ProximitySharingSendResult =
        ProximitySharingTransformer.mapSendRequestedDocuments(repository.sendRequestedDocuments())
            .also { result ->
                when (result) {
                    is ProximitySharingSendResult.Failure ->
                        Log.d(TAG, "Proximity: Sending failed ${result.error}")

                    ProximitySharingSendResult.Success ->
                        Log.d(TAG, "Proximity: Data successfully sent")
                }
            }

    override fun getSharedDocuments(): Flow<ProximitySharingSuccessResult> =
        flow {
            val disclosed = repository.getDisclosedDocuments() ?: emptyList()
            val docMap = disclosed.associate {
                it.documentId to (repository.getDocumentById(it.documentId) as? IssuedDocument)
            }.filterValues { it != null }
            emit(ProximitySharingSuccessResult.Success(disclosed, docMap))
        }.safeAsync {
            ProximitySharingSuccessResult.Failed(
                errorMessage = it.localizedMessage ?: "unknown error"
            )
        }.onEach { state ->
            when (state) {
                is ProximitySharingSuccessResult.Failed ->
                    Log.d(TAG, "Proximity: Fail to fetch shared data: ${state.errorMessage}")

                is ProximitySharingSuccessResult.Success ->
                    Log.d(
                        TAG,
                        "Proximity: Shared data: disclosed=${state.disclosedDocuments.size}, " +
                            "documentIds=${state.documentMap.keys}"
                    )
            }
        }

    override fun stopPresentation() {
        repository.stopPresentation()
    }

    private companion object {
        private const val TAG = "PresentationFlow"
    }
}
