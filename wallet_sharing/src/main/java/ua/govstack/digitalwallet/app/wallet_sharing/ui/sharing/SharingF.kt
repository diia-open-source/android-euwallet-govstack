package ua.govstack.digitalwallet.app.wallet_sharing.ui.sharing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.openLink
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForNavigationResultOnce
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForTemplateDialogNavResult
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.pin.util.PinConst
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SlideBarAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SlideBarAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.BaseScreen
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.firstOrNull
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.ui_base.util.navigation.openTemplateDialog
import ua.govstack.digitalwallet.app.wallet_core.R
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst

@AndroidEntryPoint
class SharingF : Fragment() {
    private var composeView: ComposeView? = null
    val viewModel: SharingVM by viewModels()
    private val args: SharingFArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        composeView = ComposeView(requireContext())
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView?.setContent {
            val topGroup = viewModel.toolbarData
            val body = viewModel.bodyData
            val bottom = viewModel.bottomData
            val snackBar = viewModel.snackBarData
            val loader by viewModel.loader.collectAsState(initial = Loader.create())
            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                if (args.presentationLink != null && args.presentationLink?.isNotEmpty() == true) {
                    viewModel.onUIAction(
                        UIAction(
                            actionKey = WalletConst.START_REMOTE_PRESENTATION,
                            data = args.presentationLink
                        )
                    )
                } else {
                    viewModel.onUIAction(
                        UIAction(
                            actionKey = WalletConst.START_PROXIMITY_PRESENTATION
                        )
                    )
                }
            }

            LaunchedEffect(Unit) {
                viewModel.showSnackBarEvent.collectLatest {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar("")
                }
            }

            viewModel.apply {
                navigation.collectAsEffect { navigation ->
                    when (navigation) {
                        is BaseNavigation.Back -> {
                            findNavController().popBackStack()
                        }

                        is SharingVM.SharingNavigation.OpenBrowser -> {
                            openLink(navigation.link)
                            findNavController().popBackStack()
                        }

                        is SharingVM.SharingNavigation.OpenPin -> {
                           viewModel.navigateToPinInput(this@SharingF, true)
                        }
                    }
                }

                showTemplateDialog.collectAsEffect {
                    openTemplateDialog(it.peekContent())
                }

                registerForNavigationResultOnce<Boolean>(PinConst.PIN_RESULT) { success ->
                    if (success) {
                        viewModel.onUIAction(UIAction(WalletConst.PIN_ACCEPTED))
                    }
                }
            }

            registerForTemplateDialogNavResult { action ->
                findNavController().popBackStack()
                when (action) {
                    ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()
                    ActionsConst.ERROR_DIALOG_DEAL_WITH_IT -> findNavController().popBackStack()
                    ActionsConst.DIALOG_ACTION_PREV_SCREEN -> navigateToDocuments()
                    WalletConst.TO_DOCUMENT_GALLERY_ACTION -> navigateToDocuments()
                }
            }

            BaseScreen(
                loader = loader,
                toolbar = topGroup,
                body = body,
                bottom = bottom,
                onEvent = {
                    viewModel.onUIAction(it)
                }
            )

            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { _ ->
                    snackBar.firstOrNull<UIElementData>()?.let {
                        if (it is SlideBarAtmData) {
                            SlideBarAtm(data = it)
                        }
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun navigateToDocuments() {
        findNavController().navigate(
            R.id.nav_home,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_home, inclusive = true)
                .setLaunchSingleTop(true)
                .build()
        )
    }
}
