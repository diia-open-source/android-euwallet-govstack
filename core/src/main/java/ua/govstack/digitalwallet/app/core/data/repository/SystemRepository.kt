package ua.govstack.digitalwallet.app.core.data.repository

interface SystemRepository {

    suspend fun getAppVersionCode(): Int?
    suspend fun setAppVersionCode(code: Int)
}