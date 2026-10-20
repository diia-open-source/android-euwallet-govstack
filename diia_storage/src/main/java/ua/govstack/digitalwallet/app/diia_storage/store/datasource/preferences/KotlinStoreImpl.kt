package ua.govstack.digitalwallet.app.diia_storage.store.datasource.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull

class KotlinStoreImpl(private val appContext: Context) : PreferenceDataSource {

    private val gson = Gson()

    private companion object {
        const val PREFERENCES_STORAGE = "preferences_storage"
        const val EMPTY = ""
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_STORAGE
    )

    override suspend fun setBoolean(key: String, value: Boolean) {
        val booleanKey = booleanPreferencesKey(key)
        appContext.dataStore.edit { preferences ->
            preferences[booleanKey] = value
        }
    }

    override suspend fun getBoolean(key: String): Boolean {
        val booleanKey = booleanPreferencesKey(key)
        return appContext.dataStore.data.firstOrNull()
            ?.get(booleanKey) ?: false
    }

    override suspend fun setString(key: String, value: String) {
        val stringKey = stringPreferencesKey(key)
        appContext.dataStore.edit { preferences ->
            preferences[stringKey] = value
        }
    }

    override suspend fun getString(key: String): String {
        val stringKey = stringPreferencesKey(key)
        return appContext.dataStore.data.firstOrNull()
            ?.get(stringKey) ?: EMPTY
    }

    override suspend fun <T> saveMap(key: String, map: Map<String, T>) {
        val stringKey = stringPreferencesKey(key)
        appContext.dataStore.edit { preferences ->
            val json = gson.toJson(map)
            preferences[stringKey] = json
        }
    }

    override suspend fun <T> getMap(key: String, clazz: Class<T>): Map<String, T> {
        val mapKey = stringPreferencesKey(key)
        val data = appContext.dataStore.data.firstOrNull()
            ?.get(mapKey) ?: EMPTY

        return run {
            val type = TypeToken.getParameterized(
                Map::class.java,
                String::class.java,
                clazz
            ).type
            gson.fromJson(data, type)
        } ?: emptyMap()
    }


}