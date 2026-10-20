package ua.govstack.digitalwallet.app.wallet_sharing.domain.interactor

import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RequestUriConfig
import ua.govstack.digitalwallet.app.wallet_sharing.data.repository.ProximityRepository
import ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer.ProximityQRTransformer

sealed class ProximityQRResult {
    data class QrReady(val qrCode: String) : ProximityQRResult()
    data class Error(val error: String) : ProximityQRResult()
    data object Connected : ProximityQRResult()
    data object Disconnected : ProximityQRResult()
}
interface ProximityQRInteractor {
    fun startQrEngagement(): Flow<ProximityQRResult>
    fun toggleNfcEngagement(
        componentActivity: ComponentActivity,
        toggle: Boolean
    )

    fun cancelTransfer()
    fun setConfig(config: RequestUriConfig)
}

internal class ProximityQRInteractorImpl(
    private val repository: ProximityRepository,
) : ProximityQRInteractor {

    override fun setConfig(config: RequestUriConfig) {
        repository.setConfig(config)
    }

    override fun startQrEngagement(): Flow<ProximityQRResult> =
        repository.startQrEngagement()
            .mapNotNull { ProximityQRTransformer.mapQrEngagement(it) }
            .safeAsync {
                ProximityQRResult.Error(
                    error = it.localizedMessage ?: "unknown error"
                )
            }

    override fun toggleNfcEngagement(
        componentActivity: ComponentActivity,
        toggle: Boolean
    ) {
        repository.toggleNfcEngagement(componentActivity, toggle)
    }

    override fun cancelTransfer() {
        repository.stopPresentation()
    }
}