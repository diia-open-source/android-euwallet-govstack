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
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.PresentationRepository
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingLoadingResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingRequestResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingSendResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingSuccessResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RequestUriConfig
import ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer.RemoteSharingTransformer
import java.net.URI

interface RemoteSharingInteractor {
    fun getRequestDocuments(): Flow<RemoteSharingRequestResult>
    fun updateRequestedDocuments(documents: DisclosedDocuments)
    fun setConfig(config: RequestUriConfig)

    fun observeResponse(): Flow<RemoteSharingLoadingResult>
    fun sendRequestedDocuments(): RemoteSharingSendResult

    val redirectUri: URI?
    fun getSharedDocuments(): Flow<RemoteSharingSuccessResult>
    fun stopPresentation()
}

internal class RemoteSharingInteractorImpl(
    private val resourceProvider: ResourceProvider,
    private val uuidProvider: UuidProvider,
    private val repository: PresentationRepository,
    private val walletCoreConfig: WalletCoreConfig,
) : RemoteSharingInteractor {

    override val redirectUri: URI?
        get() = repository.redirectUri

    private val genericErrorMsg
        get() = resourceProvider.genericErrorMessage()

    override fun getRequestDocuments(): Flow<RemoteSharingRequestResult> =
        repository.events.mapNotNull { response ->
            RemoteSharingTransformer.transform(
                response = response,
                repository = repository,
                resourceProvider = resourceProvider,
                uuidProvider = uuidProvider,
            )
        }.mapNotNull { result ->
            when (result) {
                is RemoteSharingRequestResult.Success -> {
                    if (
                        !walletCoreConfig.trustHandlingConfig.allowUntrustedVerifiers &&
                        !result.verifierIsTrusted
                    ) {
                        RemoteSharingRequestResult.UntrustedVerifier(result.verifierName)
                    } else {
                        result
                    }
                }

                is RemoteSharingRequestResult.NoData -> {
                    if (
                        !walletCoreConfig.trustHandlingConfig.allowUntrustedVerifiers &&
                        !result.verifierIsTrusted
                    ) {
                        RemoteSharingRequestResult.UntrustedVerifier(result.verifierName)
                    } else {
                        result
                    }
                }

                is RemoteSharingRequestResult.Failure -> {
                    if (
                        !walletCoreConfig.trustHandlingConfig.allowUntrustedVerifiers &&
                        result.error.isUntrustedVerifierResolutionFailure()
                    ) {
                        RemoteSharingRequestResult.UntrustedVerifier(verifierName = null)
                    } else {
                        result
                    }
                }

                else -> result
            }
        }.safeAsync {
            RemoteSharingRequestResult.Failure(
                error = it.localizedMessage ?: genericErrorMsg,
            )
        }.onEach { result ->
            when (result) {
                is RemoteSharingRequestResult.Failure ->
                    Log.d(TAG, "Remote: Fail to handle request ${result.error}")

                is RemoteSharingRequestResult.Success ->
                    Log.d(TAG, "Remote: InteractorResult Success")

                is RemoteSharingRequestResult.UntrustedVerifier ->
                    Log.d(TAG, "Remote: Untrusted verifier ${result.verifierName}")

                RemoteSharingRequestResult.Disconnect ->
                    Log.d(TAG, "Remote: Disconnect")

                is RemoteSharingRequestResult.NoData ->
                    Log.d(TAG, "Remote: No data to share")
            }
        }

    override fun updateRequestedDocuments(documents: DisclosedDocuments) {
        repository.updateRequestedDocuments(documents)
    }

    override fun setConfig(config: RequestUriConfig) {
        repository.setConfig(config)
    }

    override fun observeResponse(): Flow<RemoteSharingLoadingResult> =
        repository.observeSentDocumentsRequest()
            .mapNotNull { RemoteSharingTransformer.mapObserveResponse(it) }
            .mapNotNull { state ->
                if (
                    state is RemoteSharingLoadingResult.Failure &&
                    !walletCoreConfig.trustHandlingConfig.allowUntrustedVerifiers &&
                    state.error.isUntrustedVerifierResolutionFailure()
                ) {
                    null
                } else {
                    state
                }
            }
            .onEach { state ->
                when (state) {
                    is RemoteSharingLoadingResult.Failure ->
                        Log.d(TAG, "Remote: Fail to handle sharing request: ${state.error}")

                    is RemoteSharingLoadingResult.Redirect ->
                        Log.d(TAG, "Remote: Redirect to ${state.uri}")

                    RemoteSharingLoadingResult.RequestReadyToBeSent ->
                        Log.d(TAG, "Remote: RequestReadyToBeSent")

                    RemoteSharingLoadingResult.Success ->
                        Log.d(TAG, "Remote: Response successfully sent")
                }
            }

    override fun sendRequestedDocuments(): RemoteSharingSendResult =
        RemoteSharingTransformer.mapSendDocuments(repository.sendRequestedDocuments())
            .also { result ->
                when (result) {
                    RemoteSharingSendResult.Success ->
                        Log.d(TAG, "Remote: Data successfully sent")

                    is RemoteSharingSendResult.Failure ->
                        Log.d(TAG, "Remote: Sending failed ${result.error}")
                }
            }

    override fun getSharedDocuments(): Flow<RemoteSharingSuccessResult> = flow {
        val disclosed = repository.getDisclosedDocuments() ?: emptyList()
        val docMap = disclosed.associate {
            it.documentId to (repository.getDocumentById(it.documentId) as? IssuedDocument)
        }.filterValues { it != null }
        emit(RemoteSharingSuccessResult.Success(disclosed, docMap))
    }.safeAsync {
        RemoteSharingSuccessResult.Failed(it.localizedMessage ?: "unknown error")
    }.onEach { state ->
        when (state) {
            is RemoteSharingSuccessResult.Failed ->
                Log.d(TAG, "Remote: Fail to fetch shared data: ${state.errorMessage}")

            is RemoteSharingSuccessResult.Success ->
                Log.d(
                    TAG,
                    "Remote: Shared data: disclosed=${state.disclosedDocuments.size}, " +
                        "documentIds=${state.documentMap.keys}"
                )
        }
    }

    override fun stopPresentation() {
        repository.stopPresentation()
    }

    private companion object {
        private const val TAG = "RemoteSharingFlow"
    }
}

private fun String.isUntrustedVerifierResolutionFailure(): Boolean {
    val normalized = lowercase()
    return normalized.contains("untrusted x5c") ||
        (normalized.contains("invalidjarjwt") && normalized.contains("x5c")) ||
        (normalized.contains("invalid resolution") && normalized.contains("x5c"))
}
