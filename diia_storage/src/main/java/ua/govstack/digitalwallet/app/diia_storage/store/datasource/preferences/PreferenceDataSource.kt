package ua.govstack.digitalwallet.app.diia_storage.store.datasource.preferences

interface PreferenceDataSource {

    suspend fun setBoolean(key: String, value: Boolean)

    suspend fun getBoolean(key: String): Boolean

    suspend fun setString(key: String, value: String)

    suspend fun getString(key: String): String

    suspend fun <T> saveMap(key: String, map: Map<String, T>)

    suspend fun <T> getMap(key: String, clazz: Class<T>): Map<String, T>
}