package ua.govstack.digitalwallet.app.menu.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.models.SystemDialog
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.collapseApp
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.findNavControllerById
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForTemplateDialogNavResult
import ua.govstack.digitalwallet.app.menu.NavMenuActionsDirections.Companion.actionHomeFToSettingsF
import ua.govstack.digitalwallet.app.menu.R
import ua.govstack.digitalwallet.app.menu.helper.MenuHelper
import ua.govstack.digitalwallet.app.menu.models.EventType
import ua.govstack.digitalwallet.app.ui_base.NavSystemDialogDirections
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.HomeScreenTab
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.fragments.dialog.system.DiiaSystemDFVM
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation
import javax.inject.Inject

@AndroidEntryPoint
class MenuFCompose : Fragment() {

    @Inject
    lateinit var menuHelper: MenuHelper

    private var composeView: ComposeView? = null
    private val viewModel: MenuComposeVM by viewModels()
    private lateinit var event: EventType
    private val dialogVM: DiiaSystemDFVM by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.configureTopBar()
        viewModel.configureBody()
    }

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
            val topBar = viewModel.topBarData
            val body = viewModel.bodyData
            viewModel.navigation.collectAsEffect { navigation ->
                when (navigation) {
                    is BaseNavigation.Back -> {
                        activity?.collapseApp()
                    }
                }
            }
            viewModel.settingsAction.collectAsEffect {

                when (val action = it.getContentIfNotHandled()) {

                    MenuAction.OpenSettings -> {
                        navigate(
                            actionHomeFToSettingsF(),
                            findNavControllerById(R.id.nav_host)
                        )
                    }

                    MenuAction.Logout -> {
                        event = EventType.LOGOUT
                        navigate(
                            NavSystemDialogDirections.actionGlobalToSystemDialog(
                                SystemDialog(
                                    getString(R.string.settings_dialog_title_logout),
                                    getString(R.string.settings_dialog_logout_text),
                                    getString(R.string.settings_dialog_logout_stay),
                                    getString(R.string.settings_dialog_logout_leave),
                                )
                            ),
                            findNavControllerById(R.id.nav_host)
                        )
                    }

                    else -> {
                        //no-op
                    }
                }
            }

            dialogVM.action.observe(viewLifecycleOwner) {
                when (it.getContentIfNotHandled()) {
                    DiiaSystemDFVM.Action.NEGATIVE -> {
                        if (event == EventType.LOGOUT) {
                            viewModel.logoutApprove()
                        }
                    }

                    DiiaSystemDFVM.Action.POSITIVE -> {
                        //no-op
                    }

                    else -> {}
                }

            }

            registerForTemplateDialogNavResult { action ->
                findNavController().popBackStack()
                when (action) {
                    ActionsConst.GENERAL_RETRY -> viewModel.retryLastAction()
                }
            }

            HomeScreenTab(
                topBar = topBar,
                body = body,
                onEvent = {
                    viewModel.onUIAction(it)
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }
}
