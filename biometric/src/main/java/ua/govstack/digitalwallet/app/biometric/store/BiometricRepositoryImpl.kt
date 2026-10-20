package ua.govstack.digitalwallet.app.biometric.store

import kotlinx.coroutines.withContext
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.diia_storage.store.Preferences
import ua.govstack.digitalwallet.app.diia_storage.DiiaStorage
import javax.inject.Inject
import javax.inject.Singleton

class BiometricRepositoryImpl @Inject constructor(
    private val diiaStorage: DiiaStorage,
    private val dispatcherProvider: DispatcherProvider
): BiometricRepository {

    override suspend fun enableBiometricAuth(enable: Boolean) {
        withContext(dispatcherProvider.work) {
            diiaStorage.set(Preferences.UseTouchId, enable)
        }
    }

    override suspend fun isBiometricAuthEnabled(): Boolean = withContext(dispatcherProvider.work) {
        diiaStorage.getBoolean(Preferences.UseTouchId, false)
    }
}