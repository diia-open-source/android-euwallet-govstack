package ua.govstack.digitalwallet.app.biometric.store

interface BiometricRepository {

    suspend fun enableBiometricAuth(enable: Boolean)

    suspend fun isBiometricAuthEnabled(): Boolean

}