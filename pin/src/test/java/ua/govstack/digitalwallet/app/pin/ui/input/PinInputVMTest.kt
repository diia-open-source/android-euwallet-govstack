package ua.govstack.digitalwallet.app.pin.ui.input

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import ua.govstack.digitalwallet.app.core.models.UserType
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.delegation.WithRetryLastAction
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.diia_storage.store.repository.authorization.AuthorizationRepository
import ua.govstack.digitalwallet.app.pin.helper.PinHelper
import ua.govstack.digitalwallet.app.pin.repository.LoginPinRepository
import ua.govstack.digitalwallet.app.pin.rules.MainDispatcherRule
import ua.govstack.digitalwallet.app.pin.util.AndroidClientAlertDialogsFactory
import ua.govstack.digitalwallet.app.pin.utils.StubErrorHandlerOnFlow
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose.PIN_CREATED_NUM_BUTTON_ORGANISM
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.NumButtonTileOrganismData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider

@RunWith(Parameterized::class)
class PinInputVMTest(private val isVerification: Boolean) {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val actionLogout = MutableLiveData<UiEvent>()

    @Mock
    lateinit var loginPinRepository: LoginPinRepository

    @Mock
    lateinit var pinHelper: PinHelper

    @Mock
    lateinit var authorizationRepository: AuthorizationRepository

    @Mock
    lateinit var clientAlertDialogsFactory: AndroidClientAlertDialogsFactory

    @Mock
    lateinit var retryLastAction: WithRetryLastAction

    private val actionRunDeepLinkAfterPin: MutableSharedFlow<UiEvent> = MutableSharedFlow()

    @Mock
    lateinit var resourceProvider: ResourceProvider

    @Mock
    lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: PinInputVM

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginPinRepository.stub {
            var pinTryCount = 0
            onBlocking { getPinTryCount() }.thenAnswer { pinTryCount }
            onBlocking { setPinTryCount(any()) }.thenAnswer {
                pinTryCount = it.getArgument(0)
                Unit
            }
        }
        authorizationRepository.stub {
            onBlocking { getUserType() }.thenReturn(UserType.PRIMARY_USER)
        }

        viewModel = PinInputVM(
            actionLogout = actionLogout,
            loginPinRepository = loginPinRepository,
            pinHelper = pinHelper,
            clientAlertDialogsFactory = clientAlertDialogsFactory,
            errorHandling = StubErrorHandlerOnFlow(),
            retryLastAction = retryLastAction,
            actionRunDeepLinkAfterPin = actionRunDeepLinkAfterPin,
            resourceProvider = resourceProvider,
            savedStateHandle = savedStateHandle

        )
        viewModel.doInit(verification = isVerification)
    }

    @Test
    fun `initial state`() = runTest {
        val snapshot = viewModel.uiData.toList()
        Assert.assertEquals(3, snapshot.size)
        Assert.assertTrue(snapshot[0] is NavigationPanelMlcData)
        Assert.assertTrue(snapshot[1] is NumButtonTileOrganismData)
        Assert.assertTrue(snapshot[2] is TextLabelMlcData)
    }

    @Test
    fun `alternative auth enabled`() = runTest {
        whenever(pinHelper.isAlternativeAuthEnabled()).thenReturn(true)
        viewModel.navigation.test {
            viewModel.checkForAlternativeAuth()
            Assert.assertTrue(awaitItem() is PinInputVM.Navigation.AlternativeAuth)
        }
    }

    @Test
    fun `alternative auth success`() = runTest {
        viewModel.navigation.test {
            viewModel.onAlternativeAuthSuccessful()
            val expected = if (isVerification) {
                PinInputVM.Navigation.PinApproved
            } else {
                PinInputVM.Navigation.ToHome
            }
            Assert.assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `alternative auth failed`() = runTest {
        whenever(pinHelper.isAlternativeAuthEnabled()).thenReturn(true)
        viewModel.checkForAlternativeAuth()
        repeat(6) { attempt ->
            viewModel.onAlternativeAuthFailed()
            advanceUntilIdle()
            Assert.assertEquals(
                attempt < 5,
                viewModel.uiData.firstNotNullOf { it as? NumButtonTileOrganismData }.hasBiometric
            )
        }
    }

    @Test
    fun `launch alternative auth`() = runTest {
        viewModel.navigation.test {
            viewModel.onUIAction(UIAction(UIActionKeysCompose.TOUCH_ID_BUTTON))
            Assert.assertEquals(PinInputVM.Navigation.AlternativeAuth, awaitItem())
        }
    }

    @Test
    fun `valid pin input`() = runTest {
        whenever(loginPinRepository.isPinValid("1234")).thenReturn(true)
        viewModel.navigation.test {
            viewModel.onUIAction(UIAction(PIN_CREATED_NUM_BUTTON_ORGANISM, "1234"))
            val expected = if (isVerification) {
                PinInputVM.Navigation.PinApproved
            } else {
                PinInputVM.Navigation.ToHome
            }
            Assert.assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun `show reset pin`() = runTest {
        viewModel.showTemplateDialog.test {
            viewModel.onUIAction(UIAction(UIActionKeysCompose.TEXT_LABEL_MLC))
            Assert.assertEquals(
                ActionsConst.DIALOG_ACTION_CODE_LOGOUT,
                awaitItem().getContentIfNotHandled()?.data?.alternativeButton?.action
            )
        }
    }

    @Test
    fun `reset pin`() = runTest {
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

    @Test
    fun `empty actions`() = runTest {
        viewModel.onUIAction(UIAction(PIN_CREATED_NUM_BUTTON_ORGANISM))
        advanceUntilIdle()
        verify(loginPinRepository, never()).isPinValid(any())
    }

    companion object {

        @JvmStatic
        @Parameters
        fun parameters() = listOf(true, false)
    }
}
