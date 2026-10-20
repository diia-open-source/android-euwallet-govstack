package ua.govstack.digitalwallet.app.opensource.di.delegate

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ua.govstack.digitalwallet.app.core.models.ContextMenuField
import ua.govstack.digitalwallet.app.core.util.delegation.WithContextMenu
import ua.govstack.digitalwallet.app.core.util.delegation.WithDeeplinkHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandling
import ua.govstack.digitalwallet.app.core.util.delegation.WithErrorHandlingOnFlow
import ua.govstack.digitalwallet.app.core.util.delegation.WithPermission
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.opensource.util.delegation.DefaultDeeplinkHandleBehaviour
import ua.govstack.digitalwallet.app.opensource.util.delegation.DefaultErrorHandlingBehaviour
import ua.govstack.digitalwallet.app.opensource.util.delegation.DefaultErrorHandlingBehaviourOnFlow
import ua.govstack.digitalwallet.app.opensource.util.delegation.DefaultRetryLastActionBehaviour
import ua.govstack.digitalwallet.app.opensource.util.delegation.DefaultSelfPermissionBehavior
import ua.govstack.digitalwallet.app.opensource.util.delegation.DefaultWithContextMenuBehaviour

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelDelegationModule {

    @Binds
    fun bindContextMenuDelegate(
        impl: DefaultWithContextMenuBehaviour<ContextMenuField>
    ): WithContextMenu<ContextMenuField>

    @Binds
    fun bindErrorHandlerDelegate(
        impl: DefaultErrorHandlingBehaviour
    ): WithErrorHandling

    @Binds
    fun bindRetryDelegate(
        impl: DefaultRetryLastActionBehaviour
    ): WithRetryLastAction

    @Binds
    fun bindDeeplinkHandler(
        impl: DefaultDeeplinkHandleBehaviour
    ): WithDeeplinkHandling

    @Binds
    fun bindErrorHandlerDelegateOnFlow(
        impl: DefaultErrorHandlingBehaviourOnFlow
    ): WithErrorHandlingOnFlow

    @Binds
    fun bindPermissionDelegate(
        impl: DefaultSelfPermissionBehavior
    ): WithPermission

}