package ua.govstack.digitalwallet.app.diia_storage.store

import com.squareup.moshi.JsonAdapter
import ua.govstack.digitalwallet.app.diia_storage.model.KeyValueStore
import ua.govstack.digitalwallet.app.diia_storage.model.PreferenceKey

abstract class AbstractKeyValueDataSource<T>(val store: KeyValueStore) {

    protected abstract val preferenceKey: PreferenceKey
    protected abstract val jsonAdapter: JsonAdapter<T>

    open fun saveDataToStore(data: T) {
        store.set(preferenceKey, jsonAdapter.toJson(data))
    }

    fun loadData(): T? {
        if (store.containsKey(preferenceKey)) {
            try {
                return jsonAdapter.fromJson(store.getString(preferenceKey, Preferences.DEF))
            } catch (e: Exception) {
                store.set(preferenceKey, "")
                e.printStackTrace()
            }
        }
        return null
    }

}