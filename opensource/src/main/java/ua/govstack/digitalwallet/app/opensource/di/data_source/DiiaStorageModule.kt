package ua.govstack.digitalwallet.app.opensource.di.data_source

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.diia_storage.DiiaStorage
import ua.govstack.digitalwallet.app.diia_storage.PreferenceConfiguration
import ua.govstack.digitalwallet.app.diia_storage.SecureDiiaStorage
import ua.govstack.digitalwallet.app.diia_storage.store.Preferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiiaStorageModule {

    @Provides
    @Singleton
    fun provideDiiaStorage(
        @ApplicationContext context: Context
    ): DiiaStorage {
        val preferenceConfiguration = PreferenceConfiguration(
            _preferenceName = Preferences.Settings.NAME_DIIA,
            preferenceNamePrefix = "",
            allowedScopes = setOf(
                Preferences.Scopes.PIN_SCOPE,
                Preferences.Scopes.USER_SCOPE,
                Preferences.Scopes.AUTH_SCOPE
            )
        )
        return SecureDiiaStorage(context, preferenceConfiguration)
    }


}