package ua.govstack.digitalwallet.app.ui_base.fragments.dynamicdialog

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.models.ConsumableString
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.setNavigationResult
import ua.govstack.digitalwallet.app.ui_base.UiBaseConst
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.screen.TemplateDialogScreen
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.screen.TemplateMlcV2

@AndroidEntryPoint
class TemplateDialogF : DialogFragment() {

    private val viewModel: TemplateDialogVM by viewModels()
    private val navArgs: TemplateDialogFArgs by navArgs()
    private var composeView: ComposeView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)

                // Blur radius in DP
                attributes = attributes.apply { blurBehindRadius = 16 }
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        composeView = ComposeView(requireContext())
        isCancelable = false
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        composeView?.setContent {
            viewModel.apply {
                navigation.collectAsEffect { navigation ->
                    when (navigation) {
                        is TemplateDialogVM.Navigation.DismissDialog -> processDismissUiEvent()
                        is TemplateDialogVM.Navigation.OnDialogAction -> processDialogAction(
                            navigation.action
                        )
                    }
                }
            }

            val templateMlcV1Data = viewModel.templateMlcV1Data.value
            val templateMlcV2Data = viewModel.templateMlcV2Data.value

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                templateMlcV1Data?.let { lTemplateMlcV1Data ->
                    TemplateDialogScreen(
                        data = lTemplateMlcV1Data,
                        onUIAction = { viewModel.onUIAction(it) }
                    )
                }

                templateMlcV2Data?.let { lTemplateMlcV2Data ->
                    TemplateMlcV2(
                        data = lTemplateMlcV2Data,
                        onUIAction = { viewModel.onUIAction(it) }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun processDialogAction(action: String) {
        when (val resultKey = resultKey()) {
            ActionsConst.KEY_GLOBAL_PROCESSING -> {
                when (action) {
                    UiBaseConst.DIALOG_ACTION_CODE_PROLONG -> navigateToProlong()
                    ActionsConst.DIALOG_ACTION_CODE_LOGOUT -> viewModel.doLogout()
                    else -> setResult(action, resultKey)
                }
            }

            else -> setResult(action, resultKey)
        }
    }

    private fun navigateToProlong() {
        navigate(TemplateDialogFDirections.actionTemplateDialogToDestinationProlong())
    }

    private fun processDismissUiEvent() {
        setResult(ActionsConst.DIALOG_ACTION_CODE_CLOSE, resultKey())
    }

    private fun setResult(action: String, key: String) {
        //If the arbitrary ID has been set it should use the new flow with the dialog popUp behaviour
        val destinationId = navArgs.arbitaryDestinationId
        if (destinationId != -1) {
            setNavigationResult(
                arbitraryDestination = destinationId,
                data = ConsumableString(action),
                key = key
            )
            findNavController().popBackStack()
        } else {
            setNavigationResult(result = ConsumableString(action), key = key)
        }
    }

    private fun resultKey() = navArgs.dialog.key ?: ActionsConst.FRAGMENT_USER_ACTION_RESULT_KEY

}