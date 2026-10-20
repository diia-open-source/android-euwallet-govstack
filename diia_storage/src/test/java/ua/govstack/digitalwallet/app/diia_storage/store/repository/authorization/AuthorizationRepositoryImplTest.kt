package ua.govstack.digitalwallet.app.diia_storage.store.repository.authorization

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ua.govstack.digitalwallet.app.core.util.DispatcherProvider
import ua.govstack.digitalwallet.app.core.util.decoder.Base64Wrapper
import ua.govstack.digitalwallet.app.diia_storage.CommonPreferenceKeys
import ua.govstack.digitalwallet.app.diia_storage.DiiaStorage
import ua.govstack.digitalwallet.app.diia_storage.MainDispatcherRule
import ua.govstack.digitalwallet.app.diia_storage.store.datasource.preferences.PreferenceDataSource

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthorizationRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    lateinit var authorizationRepositoryImpl: AuthorizationRepositoryImpl

    lateinit var preferenceDataSource: PreferenceDataSource
    lateinit var diiaStorage: DiiaStorage
    lateinit var dispatcherProvider: DispatcherProvider
    lateinit var base64Wrapper: Base64Wrapper

    @Before
    fun before() {
        preferenceDataSource = mockk(relaxed = true)
        diiaStorage = mockk(relaxed = true)
        dispatcherProvider = mockk(relaxed = true)
        base64Wrapper = mockk(relaxed = true)

        every { dispatcherProvider.work } returns UnconfinedTestDispatcher()

        authorizationRepositoryImpl = AuthorizationRepositoryImpl(
            diiaStorage,
            dispatcherProvider,
            base64Wrapper
        )
    }

    @Test
    fun `getMobileUuid load data from storage`() = runTest {
        val mobileId = "mobile_id"

        coEvery { diiaStorage.getMobileUuid() } returns mobileId
        val result = authorizationRepositoryImpl.getMobileUuid()

        coVerify(exactly = 1) { diiaStorage.getMobileUuid() }
        assertEquals(mobileId, result)
    }

    @Test
    fun `setMobileUuid save id in storage`() = runTest {
        val mobileId = "mobile_id"

        authorizationRepositoryImpl.setMobileUuid(mobileId)

        coVerify(exactly = 1) { diiaStorage.set(CommonPreferenceKeys.UUID, mobileId) }
    }

    @Test
    fun `isUserAuthorized check if storage has token`() = runTest {
        coEvery { diiaStorage.containsKey(CommonPreferenceKeys.Token) } returns true

        assertTrue(authorizationRepositoryImpl.isUserAuthorized())

        coVerify(exactly = 1) { diiaStorage.containsKey(CommonPreferenceKeys.Token) }
    }

    @Test
    fun `logoutUser trigger logout in storage`() = runTest {
        authorizationRepositoryImpl.logoutUser()

        coVerify(exactly = 1) { diiaStorage.userLogOut() }
    }
}