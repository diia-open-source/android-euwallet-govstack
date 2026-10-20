package ua.govstack.digitalwallet.app.pin.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForTemplateDialogNavResult
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.setNavigationResult
import ua.govstack.digitalwallet.app.pin.helper.PinHelper
import ua.govstack.digitalwallet.app.pin.ui.input.compose.PinInputScreen
import ua.govstack.digitalwallet.app.pin.util.PinConst
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.util.navigation.openTemplateDialog
import javax.inject.Inject

@AndroidEntryPoint
class PinInputF : Fragment() {

    private val viewModel: PinInputVM by viewModels()
    private var composeView: ComposeView? = null

    @Inject
    lateinit var pinHelper: PinHelper

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
            val uiDataElements = viewModel.uiData

            viewModel.apply {
                val validationFinished =
                    validationFinished.collectAsState(initial = UIActionKeysCompose.PAGE_LOADING_CIRCULAR to true)
                navigation.collectAsEffect { navigation ->
                    when (navigation) {
                        is PinInputVM.Navigation.PinApproved -> {
                            pinApproved()
                        }

                        is PinInputVM.Navigation.ToHome -> {
                            navigateToHome()
                        }

                        is PinInputVM.Navigation.AlternativeAuth -> {
                            pinHelper.openAlternativeAuth(this@PinInputF, viewModel)
                        }
                    }
                }
                showTemplateDialog.collectAsEffect {
                    openTemplateDialog(it.peekContent())
                }

                lifecycleScope.launch {
                    accessibilityMessage.collect { messageRes ->
                        messageRes?.let {
                            requireView().post {
                                requireView().announceForAccessibility(getString(it))
                                viewModel.clearAccessibilityMessage()
                            }
                        }
                    }
                }

                PinInputScreen(
                    data = uiDataElements,
                    contentLoaded = validationFinished.value,
                    onUIAction = { viewModel.onUIAction(it) }
                )
            }
        }

        registerForTemplateDialogNavResult { action ->
            findNavController().popBackStack()
            when (action) {
                ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()
                ActionsConst.DIALOG_ACTION_CODE_LOGOUT -> viewModel.resetPin()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun pinApproved() {
        setNavigationResult( true, PinConst.PIN_RESULT)
        findNavController().popBackStack()
    }

    private fun navigateToHome() {
        navigate(
            PinInputFDirections.actionDestinationPinInputToHomeF()
        )
    }

}