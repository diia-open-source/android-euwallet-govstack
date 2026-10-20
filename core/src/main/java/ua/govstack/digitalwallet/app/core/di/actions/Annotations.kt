package ua.govstack.digitalwallet.app.core.di.actions

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionLogout

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionDeeplink

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionAllowAuthorizedLinks

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionDocumentBackground

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionConfirmDocumentRemoval

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionFocusOnDocument

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionDeleteDocument

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionSelectedMenuItem

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionLazy

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionNetworkState

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionDeferredIssuingEvent

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionAppStartEvent

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GlobalActionRunDeepLinkAfterPin