package ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer

import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.TransferEventResult
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.PresentationRepository
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingLoadingResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingRequestResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RemoteSharingSendResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.SendRequestedDocumentsResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.WalletCoreResult

object RemoteSharingTransformer {

    fun mapObserveResponse(state: WalletCoreResult): RemoteSharingLoadingResult? =
        when (state) {
            is WalletCoreResult.Failure ->
                RemoteSharingLoadingResult.Failure(state.error)

            is WalletCoreResult.Redirect ->
                RemoteSharingLoadingResult.Redirect(state.uri)

            is WalletCoreResult.Success ->
                RemoteSharingLoadingResult.Success

            is WalletCoreResult.RequestIsReadyToBeSent ->
                RemoteSharingLoadingResult.RequestReadyToBeSent
        }

    fun mapSendDocuments(state: SendRequestedDocumentsResult): RemoteSharingSendResult =
        when (state) {
            is SendRequestedDocumentsResult.RequestSent ->
                RemoteSharingSendResult.Success

            is SendRequestedDocumentsResult.Failure ->
                RemoteSharingSendResult.Failure(state.error)
        }

    suspend fun transform(
        response: TransferEventResult,
        repository: PresentationRepository,
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider,
    ): RemoteSharingRequestResult? = when (response) {
        is TransferEventResult.RequestReceived -> mapRequestReceived(
            response = response,
            repository = repository,
            resourceProvider = resourceProvider,
            uuidProvider = uuidProvider,
        )
        is TransferEventResult.Error -> RemoteSharingRequestResult.Failure(
            error = response.error,
        )
        is TransferEventResult.Disconnected -> RemoteSharingRequestResult.Disconnect
        else -> null
    }

    private suspend fun mapRequestReceived(
        response: TransferEventResult.RequestReceived,
        repository: PresentationRepository,
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider,
    ): RemoteSharingRequestResult {
        if (response.requestData.all { it.requestedItems.isEmpty() }) {
            return RemoteSharingRequestResult.NoData(
                verifierName = response.verifierName,
                verifierIsTrusted = response.verifierIsTrusted,
            )
        }

        val documentsDomain = RequestTransformer.transformToDomainItems(
            storageDocuments = repository.getAllIssuedDocuments(),
            requestDocuments = response.requestData,
            resourceProvider = resourceProvider,
            uuidProvider = uuidProvider,
        ).getOrThrow()
            .filterNot { repository.isDocumentRevoked(it.docId) }

        return if (documentsDomain.isNotEmpty()) {
            RemoteSharingRequestResult.Success(
                verifierName = response.verifierName,
                verifierIsTrusted = response.verifierIsTrusted,
                documentPayloads = documentsDomain,
            )
        } else {
            RemoteSharingRequestResult.NoData(
                verifierName = response.verifierName,
                verifierIsTrusted = response.verifierIsTrusted,
            )
        }
    }
}
