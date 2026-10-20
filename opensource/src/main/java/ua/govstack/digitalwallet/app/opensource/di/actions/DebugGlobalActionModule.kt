package ua.govstack.digitalwallet.app.opensource.di.actions

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionLazy
import ua.govstack.digitalwallet.app.core.models.ActionDataLazy
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DebugGlobalActionModule {

    @Provides
    @Singleton
    @GlobalActionLazy
    fun provideActionLazy() = MutableSharedFlow<UiDataEvent<ActionDataLazy>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
}