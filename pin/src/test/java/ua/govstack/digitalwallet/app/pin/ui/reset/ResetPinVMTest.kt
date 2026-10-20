package ua.govstack.digitalwallet.app.pin.ui.reset

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepository
import ua.govstack.digitalwallet.app.pin.rules.MainDispatcherRule
import ua.govstack.digitalwallet.app.pin.util.AndroidClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.pin.utils.StubErrorHandlerOnFlow
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganismData
import ua.govstack.digitalwallet.app.ui_base.navigation.BaseNavigation

@RunWith(MockitoJUnitRunner::class)
class ResetPinVMTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var loginPinRepository: LoginPinRepository

    @Mock
    lateinit var clientAlertDialogsFactory: AndroidClientAlertDialogsFactory

    @Mock
    lateinit var retryLastAction: WithRetryLastAction

    private val actionLogout = MutableLiveData<UiEvent>()

    private lateinit var viewModel: ResetPinVM

    @Before
    fun setUp() {
        viewModel = ResetPinVM(
            actionLogout = actionLogout,
            loginPinRepository = loginPinRepository,
            clientAlertDialogsFactory = clientAlertDialogsFactory,
            errorHandling = StubErrorHandlerOnFlow(),
            retryLastAction = retryLastAction,
        )
    }

    @Test
    fun `initial state`() = runTest {
        val snapshot = viewModel.uiData.toList()
        Assert.assertEquals(3, snapshot.size)
        Assert.assertTrue(snapshot[0] is TopGroupOrgData)
        Assert.assertTrue(snapshot[1] is TextLabelMlcData)
        Assert.assertTrue(snapshot[2] is NumButtonTileOrganismData)
    }

    @Test
    fun `navigate back`() = runTest {
        viewModel.navigation.test {
            viewModel.onUIAction(UIAction(UIActionKeysCompose.TOOLBAR_NAVIGATION_BACK))
            Assert.assertEquals(BaseNavigation.Back, awaitItem())
        }
        viewModel.navigation.test {
            viewModel.onUIAction(UIAction(
                actionKey = UIActionKeysCompose.TITLE_GROUP_MLC,
                action = DataActionWrapper(type = ActionsConst.ACTION_NAVIGATE_BACK)
            ))
            Assert.assertEquals(BaseNavigation.Back, awaitItem())
        }
    }

    @Test
    fun `reset valid pin`() = runTest {
        whenever(loginPinRepository.isPinValid(any())).thenReturn(true)
        viewModel.navigation.test {
            viewModel.onUIAction(
                UIAction(
                    UIActionKeysCompose.PIN_CREATED_NUM_BUTTON_ORGANISM,
                    data = "1234"
                )
            )
            Assert.assertEquals(ResetPinVM.Navigation.CreateNewPin, awaitItem())
        }
    }

    @Test
    fun logout() = runTest {
        viewModel.resetPin()
        Assert.assertTrue(actionLogout.value?.notHandedYet == true)
    }


    @Test
    fun `pin cleared`() = runTest {
        viewModel.onUIAction(UIAction(UIActionKeysCompose.PIN_CLEARED_NUM_BUTTON_ORGANISM))
        val data = viewModel.uiData.firstNotNullOf { it as? NumButtonTileOrganismData }
        Assert.assertFalse(data.clearPin)
        Assert.assertFalse(data.clearWithShake)
    }
}