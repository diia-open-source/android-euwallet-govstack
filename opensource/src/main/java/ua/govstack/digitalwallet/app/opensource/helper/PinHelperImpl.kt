package ua.govstack.digitalwallet.app.opensource.helper

import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.biometric.Biometric
import ua.govstack.digitalwallet.app.biometric.store.BiometricRepository
import ua.govstack.digitalwallet.app.biometric.ui.showBiometricAuthPrompt
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.opensource.NavMainDirections
import ua.govstack.digitalwallet.app.pin.helper.PinHelper
import ua.govstack.digitalwallet.app.pin.ui.input.AlternativeAuthCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinHelperImpl @Inject constructor(
    private val biometric: Biometric,
    private val biometricRepository: BiometricRepository
) : PinHelper {

    override fun isAlternativeAuthAvailable(): Boolean {
        return biometric.isBiometricAuthAvailable()
    }

    override suspend fun isAlternativeAuthEnabled(): Boolean {
        return biometric.isBiometricAuthAvailable() && biometricRepository.isBiometricAuthEnabled()
    }

    override fun openAlternativeAuth(host: Fragment, callback: AlternativeAuthCallback) {
        host.showBiometricAuthPrompt(biometric.getPromptDescriptionString()) { result ->
            if (result) {
                callback.onAlternativeAuthSuccessful()
            } else {
                callback.onAlternativeAuthFailed()
            }
        }
    }

    override fun navigateToAlternativeAuthSetup(
        host: Fragment,
        resultDestinationId: Int,
        resultKey: String,
        pin: String,
    ) {
        host.navigate(
            NavMainDirections.actionGlobalToSetupBiometric(
                resultDestinationId = resultDestinationId,
                resultKey = resultKey,
                pin = pin,
            )
        )
    }

}