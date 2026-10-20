package ua.govstack.digitalwallet.app.diia_storage.store.repository.system

import kotlinx.coroutines.withContext
import ua.govstack.digitalwallet.app.core.data.repository.SystemRepository
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.diia_storage.CommonPreferenceKeys
import ua.govstack.digitalwallet.app.diia_storage.DiiaStorage
import javax.inject.Inject

class SystemRepositoryImpl @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val diiaStorage: DiiaStorage
) : SystemRepository {

    override suspend fun getAppVersionCode(): Int? = withContext(dispatcherProvider.work) {
        val code = diiaStorage.getInt(CommonPreferenceKeys.CurrentAppVersion, -1)
        if (code == -1) null else code
    }

    override suspend fun setAppVersionCode(code: Int) {
        withContext(dispatcherProvider.work) {
            diiaStorage.set(CommonPreferenceKeys.CurrentAppVersion, code)
        }
    }
}