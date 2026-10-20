package ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer

import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.TransferEventResult
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.ProximityRepository
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingLoadingResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingRequestResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.ProximitySharingSendResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.SendRequestedDocumentsResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.WalletCoreResult

object ProximitySharingTransformer {

    fun mapObserveResponse(
        response: WalletCoreResult,
    ): ProximitySharingLoadingResult? = when (response) {
        is WalletCoreResult.Failure ->
            ProximitySharingLoadingResult.Failure(response.error)

        is WalletCoreResult.Redirect -> null

        is WalletCoreResult.Success ->
            ProximitySharingLoadingResult.Success

        is WalletCoreResult.RequestIsReadyToBeSent ->
            ProximitySharingLoadingResult.RequestReadyToBeSent
    }

    fun mapSendRequestedDocuments(
        result: SendRequestedDocumentsResult,
    ): ProximitySharingSendResult = when (result) {
        is SendRequestedDocumentsResult.RequestSent ->
            ProximitySharingSendResult.Success

        is SendRequestedDocumentsResult.Failure ->
            ProximitySharingSendResult.Failure(result.error)
    }

    suspend fun mapRequestEvent(
        response: TransferEventResult,
        repository: ProximityRepository,
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider,
    ): ProximitySharingRequestResult? = when (response) {
        is TransferEventResult.RequestReceived ->
            mapRequestReceived(
                response = response,
                repository = repository,
                resourceProvider = resourceProvider,
                uuidProvider = uuidProvider,
            )

        is TransferEventResult.Error ->
            ProximitySharingRequestResult.Failure(response.error)

        is TransferEventResult.Disconnected ->
            ProximitySharingRequestResult.Disconnect

        else -> null
    }

    private suspend fun mapRequestReceived(
        response: TransferEventResult.RequestReceived,
        repository: ProximityRepository,
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider,
    ): ProximitySharingRequestResult {
        if (response.requestData.all { it.requestedItems.isEmpty() }) {
            return ProximitySharingRequestResult.NoData(
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
            ProximitySharingRequestResult.Success(
                verifierName = response.verifierName,
                verifierIsTrusted = response.verifierIsTrusted,
                documentPayloads = documentsDomain,
            )
        } else {
            ProximitySharingRequestResult.NoData(
                verifierName = response.verifierName,
                verifierIsTrusted = response.verifierIsTrusted,
            )
        }
    }
}
