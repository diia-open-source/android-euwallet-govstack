package ua.govstack.digitalwallet.app.wallet_core.issuance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import ua.govstack.digitalwallet.app.wallet_core.issuance.deferred.DeferredIssuingResultDispatcher
import ua.govstack.digitalwallet.app.wallet_core.issuance.deferred.DeferredIssuingResultDispatcherImpl
import ua.govstack.digitalwallet.app.wallet_core.issuance.deferred.DeferredIssuingResultHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeferredIssuingDispatchBindingsModule {
    @Multibinds
    abstract fun bindDeferredIssuingResultHandlers(): Set<DeferredIssuingResultHandler>
}

@Module
@InstallIn(SingletonComponent::class)
object DeferredIssuingDispatchModule {
    @Provides
    @Singleton
    fun provideDeferredIssuingResultDispatcher(
        handlers: Set<@JvmSuppressWildcards DeferredIssuingResultHandler>,
    ): DeferredIssuingResultDispatcher = DeferredIssuingResultDispatcherImpl(handlers)
}
