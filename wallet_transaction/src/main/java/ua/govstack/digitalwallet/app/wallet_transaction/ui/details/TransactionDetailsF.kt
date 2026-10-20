package ua.govstack.digitalwallet.app.wallet_transaction.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForTemplateDialogNavResult
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.BaseScreen
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import ua.govstack.digitalwallet.app.ui_base.util.navigation.openTemplateDialog

@AndroidEntryPoint
class TransactionDetailsF : Fragment() {
    private var composeView: ComposeView? = null
    val viewModel: TransactionDetailsVM by viewModels()

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

            val loader by viewModel.loader.collectAsState(initial = Loader.create())

            viewModel.apply {
                navigation.collectAsEffect { navigation ->
                    when (navigation) {
                        is BaseNavigation.Back -> {
                            findNavController().popBackStack()
                        }
                    }
                }

                showTemplateDialog.collectAsEffect {
                    openTemplateDialog(it.peekContent())
                }
            }

            registerForTemplateDialogNavResult { action ->
                findNavController().popBackStack()
                when (action) {
                    ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()
                    ActionsConst.ERROR_DIALOG_DEAL_WITH_IT -> findNavController().popBackStack()
                    ActionsConst.DIALOG_ACTION_PREV_SCREEN -> findNavController().popBackStack()
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

}