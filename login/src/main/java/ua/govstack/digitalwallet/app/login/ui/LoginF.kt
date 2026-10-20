package ua.govstack.digitalwallet.app.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.models.ConsumableString
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.currentDestinationId
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForNavigationResultOnce
import ua.govstack.digitalwallet.app.login.ui.compose.LoginScreen
import ua.govstack.digitalwallet.app.pin.model.CreatePinFlowType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.util.navigation.openTemplateDialog

@AndroidEntryPoint
class LoginF : Fragment() {

    private val loginVM: LoginVM by viewModels()
    private var composeView: ComposeView? = null

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

        registerForNavigationResultOnce(RESULT_KEY_CREATE_PIN, loginVM::setPinCode)
        composeView?.setContent {
            loginVM.apply {

                showTemplateDialog.collectAsEffect {
                    openTemplateDialog(it.peekContent())
                }

                val isLoading = loginVM.isLoading.collectAsState(
                    initial = Pair(
                        UIActionKeysCompose.PAGE_LOADING_CIRCULAR,
                        true
                    )
                )
                navigation.collectAsEffect { navigation ->
                    when (navigation) {
                        is LoginVM.Navigation.ToPinCreation -> {
                            navigateToPinCreation()
                        }

                        is LoginVM.Navigation.ToHome -> {
                            navigateToHomeScreen()
                        }
                    }
                }

                LoginScreen(
                    toolbar = loginVM.toolbarData,
                    body = loginVM.bodyData,
                    bottom = loginVM.bottomData,
                    screenNavigationProcessing = loginVM.progressState,
                    isLoading = isLoading.value,
                    onEvent = { loginVM.onUIAction(it) }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun navigateToPinCreation() {
        navigate(
            LoginFDirections.actionLoginToCreatePin(
                resultDestinationId = currentDestinationId ?: return,
                resultKey = RESULT_KEY_CREATE_PIN,
                flowType = CreatePinFlowType.AUTHORIZATION
            )
        )
    }

    private fun navigateToHomeScreen() {
        navigate(
            LoginFDirections.actionDestinationLoginToHomeF(
                launchFlow = ConsumableString(ActionsConst.ACTION_AUTH_FLOW)
            )
        )
    }

    private companion object {
        const val RESULT_KEY_CREATE_PIN = "create_pin"
    }
}
