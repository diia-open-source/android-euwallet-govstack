package ua.govstack.digitalwallet.app.diia_storage.store.repository.authorization

import kotlinx.coroutines.withContext
import ua.govstack.digitalwallet.app.core.models.UserType
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.decoder.Base64Wrapper
import ua.govstack.digitalwallet.app.diia_storage.CommonPreferenceKeys
import ua.govstack.digitalwallet.app.diia_storage.DiiaStorage
import javax.inject.Inject

class AuthorizationRepositoryImpl @Inject constructor(
    private val diiaStorage: DiiaStorage,
    private val dispatcherProvider: DispatcherProvider,
    private val base64Wrapper: Base64Wrapper
) : AuthorizationRepository {

    private companion object {
        const val DEF_VALUE_STRING = "def_string"
    }

    override suspend fun getUserType(): UserType = UserType.PRIMARY_USER

    override suspend fun getMobileUuid(): String = withContext(dispatcherProvider.work) {
        diiaStorage.getMobileUuid()
    }

    override suspend fun setMobileUuid(uuid: String) {
        withContext(dispatcherProvider.work) {
            diiaStorage.set(CommonPreferenceKeys.UUID, uuid)
        }
    }

    override suspend fun isUserAuthorized(): Boolean = withContext(dispatcherProvider.work) {
        diiaStorage.containsKey(CommonPreferenceKeys.Token)
    }

    override suspend fun logoutUser() {
        withContext(dispatcherProvider.work) {
            diiaStorage.userLogOut()
        }
    }

    override suspend fun getToken(): String? = withContext(dispatcherProvider.work) {
        val token = diiaStorage.getString(CommonPreferenceKeys.Token, DEF_VALUE_STRING)
        if (token != DEF_VALUE_STRING) token else null
    }

    override suspend fun setToken(token: String) {
        withContext(dispatcherProvider.work) {
            diiaStorage.set(CommonPreferenceKeys.Token, token)
        }
    }


}