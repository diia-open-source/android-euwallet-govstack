package ua.govstack.digitalwallet.app.diia_storage.store.repository.authorization

import ua.govstack.digitalwallet.app.core.models.UserType

interface AuthorizationRepository {

    suspend fun getMobileUuid(): String

    suspend fun setMobileUuid(uuid: String)

    suspend fun isUserAuthorized(): Boolean

    suspend fun logoutUser()

    suspend fun setToken(token: String)

    suspend fun getToken(): String?

    suspend fun getUserType(): UserType

}