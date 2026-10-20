package ua.govstack.digitalwallet.app.wallet_issuing.ui.documentOffer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.models.ConsumableString
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.event.observeUiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForNavigationResult
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForNavigationResultOnce
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.setNavigationResult
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.pin.util.PinConst
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.BaseScreen
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.ui_base.util.navigation.openTemplateDialog
import ua.govstack.digitalwallet.app.wallet_core.R
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst

@AndroidEntryPoint
class DocumentOfferF : Fragment() {

    private val viewModel: DocumentOfferVM by viewModels()
    private var composeView: ComposeView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.handleDeepLinks()
            }
        }

        composeView = ComposeView(requireContext())
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView?.setContent {
            val topGroup = viewModel.toolbarData
            val body = viewModel.bodyData
            val bottom = viewModel.bottomData

            val loader by viewModel.loader.collectAsState(initial = Loader.create())

            viewModel.apply {

                navigation.collectAsEffect { navigation ->
                    when (navigation) {
                        is BaseNavigation.Back -> {
                            findNavController().popBackStack()
                        }

                        is DocumentOfferVM.Navigation.Home -> {
                            findNavController().popBackStack(R.id.homeF, false)
                        }

                        is DocumentOfferVM.Navigation.PreAuthPinF -> {
                            navigate(
                                DocumentOfferFDirections.actionDocumentOfferToPreAuthPinF(
                                    linkToProcess = navigation.linkToProcess
                                )
                            )
                        }
                        is DocumentOfferVM.Navigation.PinInput -> {
                            viewModel.navigateToPinInput(this@DocumentOfferF, true)
                        }
                    }
                }

                showTemplateDialog.collectAsEffect {
                    openTemplateDialog(it.peekContent())
                }

                processNavigation.observeUiDataEvent(viewLifecycleOwner) { action ->
                    navigate(action)
                }

                registerForNavigationResultOnce<Boolean>(PinConst.PIN_RESULT) { success ->
                    if (success) {
                        viewModel.onUIAction(UIAction(WalletConst.PIN_ACCEPTED))
                    }
                }
            }

            BaseScreen(
                loader = loader,
                toolbar = topGroup,
                body = body,
                bottom = bottom,
                onEvent = {
                    viewModel.onUIAction(it)
                })
        }

        registerForNavigationResult<ConsumableString>(ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY) { event ->
            event.consumeEvent { action ->
                findNavController().popBackStack()
                when (action) {
                    ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()

                    ActionsConst.DIALOG_ACTION_CANCEL,
                    ActionsConst.DIALOG_ACTION_CODE_CLOSE,
                    WalletConst.TO_DOCUMENT_GALLERY_ACTION,
                    WalletConst.TO_RECENTLY_ADDED_DOCUMENT -> navigateToDocuments()

                    ActionsConst.ERROR_DIALOG_DEAL_WITH_IT,
                    ActionsConst.DIALOG_ACTION_TO_PUBLIC_SERVICES,
                    ActionsConst.DIALOG_ACTION_PREV_SCREEN -> findNavController().popBackStack()
                }
            }
        }
    }


    private fun navigateToDocuments() {
        setNavigationResult(
            arbitraryDestination = R.id.homeF,
            key = ActionsConst.ACTION_ITEM_SELECTED,
            data = ConsumableString(ActionsConst.ACTION_MENU_DOCUMENTS)
        )
        findNavController().popBackStack(R.id.homeF, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }
}