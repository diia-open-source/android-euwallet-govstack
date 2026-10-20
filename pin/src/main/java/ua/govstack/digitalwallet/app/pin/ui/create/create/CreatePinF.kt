package ua.govstack.digitalwallet.app.pin.ui.create.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForTemplateDialogNavResult
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.setNavigationResult
import ua.govstack.digitalwallet.app.pin.ui.create.compose.CreatePinScreen
import ua.govstack.digitalwallet.app.pin.util.PinConst.ACTION_CODE_HOME
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.ui_base.util.navigation.openTemplateDialog

@AndroidEntryPoint
class CreatePinF : Fragment() {

    private val viewModel: CreatePinVM by viewModels()
    private val args: CreatePinFArgs by navArgs()
    private var composeView: ComposeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.doInit(args.flowType)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        composeView = ComposeView(requireContext())
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView?.setContent {
            val uiDataElements = viewModel.uiData

            viewModel.apply {
                navigation.collectAsEffect { navigation ->
                    when (navigation) {
                        is CreatePinVM.Navigation.ToPinConformation -> {
                            navigateToConformation(navigation.pin)
                        }

                        is BaseNavigation.Back -> {
                            findNavController().popBackStack()
                        }
                    }
                }
                showTemplateDialog.collectAsEffect {
                    openTemplateDialog(it.peekContent())
                }
            }

            CreatePinScreen(
                data = uiDataElements,
                useGradientBg = true,
                onUIAction = { viewModel.onUIAction(it) })

            registerForTemplateDialogNavResult { action ->
                if (action == ActionsConst.DIALOG_ACTION_CODE_SKIP) {
                    findNavController().popBackStack()
                }
                when (action) {
                    ACTION_CODE_HOME -> {
                        closeFlow()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun navigateToConformation(pin: String) {
        navigate(
            CreatePinFDirections.actionDestinationCreatePinToDestinationConfirmPin(
                args.resultDestinationId, args.resultKey, args.flowType, pin
            )
        )
    }

    private fun closeFlow() {
        setNavigationResult(
            arbitraryDestination = args.resultDestinationId,
            key = RESULT_KEY_BACK,
            data = ActionsConst.DIALOG_ACTION_REFRESH
        )
        findNavController().popBackStack(args.resultDestinationId, false)
    }

    private companion object {
        const val RESULT_KEY_BACK = "GenerateSignatureF.RESULT_KEY_BACK"
    }
}