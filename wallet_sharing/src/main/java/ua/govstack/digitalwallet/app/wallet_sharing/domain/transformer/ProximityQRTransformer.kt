package ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer

import ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor.ProximityQRResult
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.TransferEventResult

object ProximityQRTransformer {

    fun mapQrEngagement(
        response: TransferEventResult,
    ): ProximityQRResult? = when (response) {
        is TransferEventResult.Connected ->
            ProximityQRResult.Connected

        is TransferEventResult.Error ->
            ProximityQRResult.Error(response.error)

        is TransferEventResult.QrEngagementReady ->
            ProximityQRResult.QrReady(response.qrCode)

        is TransferEventResult.Disconnected ->
            ProximityQRResult.Disconnected

        else -> null
    }
}
