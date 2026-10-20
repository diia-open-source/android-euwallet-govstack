package ua.govstack.digitalwallet.app.wallet_gallery.ui.gallery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.models.deeplink.DeepLinkActionStartFlow
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.core.util.navigation.HomeNavigation
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.HomeScreenTab
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentAction
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.DocsConst
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.ConfirmRemoveDocBackStackResult
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.DocUserActionResult
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.DocumentsHomeNavigation
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.RemoveDocBackStackResult
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.RemoveDocumentBackStackResult
import ua.govstack.digitalwallet.app.wallet_sharing.ui.qrBottomSheet.SharingBottomSheet
import ua.govstack.digitalwallet.app.wallet_sharing.ui.qrBottomSheet.SharingBottomSheetVM
import java.math.RoundingMode
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryC(
    modifier: Modifier = Modifier,
    vm: GalleryVM,
    navigationBackStackEventFlow: Flow<BackStackEvent>,
    homeNavigationActionFlow: MutableSharedFlow<HomeNavigation>,
) {
    val body = vm.bodyData
    val sharingBottomSheetVM: SharingBottomSheetVM = hiltViewModel()

    val connectivityState = vm.connectivity.collectAsState(true)
    val sharingAvailable = vm.sharingAvailable.collectAsState(false)
    val loader by vm.loader.collectAsState(initial = Loader.create())

    vm.apply {
        navigation.collectAsEffect { navigation ->
            when (navigation) {
                is GalleryVM.Navigation.ToOnboarding -> {
                    homeNavigationActionFlow.tryEmit(
                        DocumentsHomeNavigation.ToStartNewFlow(
                            DeepLinkActionStartFlow(
                                flowId = DeepLinkRoutes.FLOW_INFO,
                                resId = navigation.docType
                            )
                        )
                    )
                }

                is GalleryVM.Navigation.ToDocActions -> {
                    homeNavigationActionFlow.tryEmit(
                        DocumentsHomeNavigation.ToDocActions(
                            navigation.doc,
                            navigation.position,
                            navigation.manualDocs
                        )
                    )
                }

                is GalleryVM.Navigation.ToDocStackOrder -> {
                    homeNavigationActionFlow.tryEmit(
                        DocumentsHomeNavigation.ToDocsOrder()
                    )
                }

                is GalleryVM.Navigation.ToCredentialList -> {
                    homeNavigationActionFlow.tryEmit(
                        DocumentsHomeNavigation.ToCredentialList()
                    )
                }
            }
        }
    }

    vm.showTemplateDialog.collectAsEffect { template ->
        vm.stopLoading()
        homeNavigationActionFlow.tryEmit(DocumentsHomeNavigation.ToTemplateDialog(template.peekContent()))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(vm, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.deferredIssuingEvents.collectLatest { event ->
                vm.handleDeferredIssuingEvent(event)
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.displayModeContent()
    }

    val navigationBackStackEvent = navigationBackStackEventFlow.collectAsStateWithLifecycle(
        initialValue = BackStackEvent.Empty
    )

    handleNavigationBackStackEvent(
        navigationBackStackEvent.value,
        vm,
        homeNavigationActionFlow
    )


    val density = LocalDensity.current
    val scaffoldState = remember(density) {
        BottomSheetScaffoldState(
            bottomSheetState = SheetState(
                skipHiddenState = true,
                density = density,
                initialValue = SheetValue.PartiallyExpanded,
                skipPartiallyExpanded = false
            ),
            snackbarHostState = SnackbarHostState()
        )
    }
    var relatedToBottomSheetProgressAlpha by remember { mutableFloatStateOf(0f) }
    BoxWithConstraints {
        val draggingProgress by calculateDraggingProgress(
            scaffoldState = scaffoldState,
            containerMaxHeight = this.constraints.maxHeight,
            peekingHeightPx = with(LocalDensity.current) { 32.dp.toPx() }
        )

        LaunchedEffect(draggingProgress) {
            relatedToBottomSheetProgressAlpha = draggingProgress.toFloat()
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 32.dp,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetDragHandle = {},
            containerColor = Color.Transparent,
            sheetContainerColor = Color.Transparent,
            sheetTonalElevation = 10.dp,
            sheetShadowElevation = 0.dp,
            sheetContent = {
                AnimatedVisibility(
                    visible = sharingAvailable.value,
                    enter = fadeIn(animationSpec = tween(350)) +
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 450)) +
                            slideOutVertically(
                                targetOffsetY = { it + it / 3 },
                                animationSpec = tween(
                                    durationMillis = 550,
                                    easing = FastOutSlowInEasing
                                )
                            )
                ) {
                    SharingBottomSheet(
                        alpha = relatedToBottomSheetProgressAlpha,
                        scaffoldState = scaffoldState,
                        vm = sharingBottomSheetVM,
                        navigateToSharingScreen = {
                            homeNavigationActionFlow.tryEmit(
                                DocumentsHomeNavigation.ToStartNewFlow(
                                    deeplink = DeepLinkActionStartFlow(
                                        flowId = DeepLinkRoutes.FLOW_PROXIMITY,
                                        resId = ""
                                    )
                                )
                            )
                        }
                    )
                }
            },
            snackbarHost = { SnackbarHost(it) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                HomeScreenTab(
                    modifier = modifier,
                    loader = loader,
                    connectivityState = connectivityState.value,
                    body = body,
                    onEvent = {
                        vm.onUIAction(it)
                    })
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = relatedToBottomSheetProgressAlpha / 2))
                )
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun calculateDraggingProgress(
    scaffoldState: BottomSheetScaffoldState,
    containerMaxHeight: Int,
    peekingHeightPx: Float,
): State<Double> {
    val dragProgress by remember {
        derivedStateOf {
            val offset =
                runCatching { scaffoldState.bottomSheetState.requireOffset() }.getOrDefault(
                    Float.MAX_VALUE
                )
            val result =
                roundOffDecimal((offset / (containerMaxHeight - peekingHeightPx)).toDouble())
            return@derivedStateOf when {
                result >= 1.0 -> 1.0
                else -> result
            }
        }
    }
    return rememberUpdatedState(newValue = roundOffDecimal(1 - dragProgress))
}

fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).replace(",", ".").toDouble()
}

fun handleNavigationBackStackEvent(
    event: BackStackEvent,
    viewModel: GalleryVM,
    homeNavigationActionFlow: MutableSharedFlow<HomeNavigation>,
) {
    when (event) {
        is BackStackEvent.UserActionResult -> {
            event.data.consumeEvent { action ->
                when (action) {
                    ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()
                    ActionsConst.ERROR_DIALOG_DEAL_WITH_IT -> viewModel.stopLoading()
                    ActionsConst.DIALOG_ACTION_DOC_CONFIRM_REMOVE -> viewModel.approveRemoveDoc()
                }
            }
        }

        is DocUserActionResult -> {
            event.data.consumeEvent<DocumentAction> { docAction ->
                when (docAction.actionKey) {
                    ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()
                    DocsConst.DOCUMENT_ACTION_START_FLOW -> {
                        val deeplink = DeepLinkActionStartFlow(
                            flowId = docAction.docType.orEmpty(),
                            resId = docAction.data.orEmpty()
                        )
                        homeNavigationActionFlow.tryEmit(
                            DocumentsHomeNavigation.ToStartNewFlow(deeplink)
                        )
                    }

                    ActionsConst.DIALOG_ACTION_DOC_CONFIRM_REMOVE,
                    DocsConst.DOCUMENT_ACTION_CONFIRM -> {
                        //no-op
                    }
                }
            }
        }

        is RemoveDocBackStackResult -> {
            event.data.consumeEvent<Document> {
                viewModel.showConfirmDeleteTemplateRemote()
            }
        }

        is RemoveDocumentBackStackResult -> {
            event.data.consumeEvent<Document> { doc ->
                viewModel.initRemoveDoc(doc)
            }
        }

        is ConfirmRemoveDocBackStackResult -> {
            event.data.consumeEvent<Document> { doc ->
                viewModel.initRemoveDoc(doc)
            }
        }
    }
}
