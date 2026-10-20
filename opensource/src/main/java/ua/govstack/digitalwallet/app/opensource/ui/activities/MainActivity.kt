package ua.govstack.digitalwallet.app.opensource.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionDeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.di.actions.GlobalActionLogout
import ua.govstack.digitalwallet.app.core.models.common.DeferredIssuingEvent
import ua.govstack.digitalwallet.app.core.util.deeplink.DeepLinkRoutes
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.event.UiEvent
import ua.govstack.digitalwallet.app.core.util.event.observeUiEvent
import ua.govstack.digitalwallet.app.opensource.R
import ua.govstack.digitalwallet.app.opensource.util.extensions.activity.overrideConfiguration
import ua.govstack.digitalwallet.app.opensource.util.extensions.activity.setUpEdgeToEdge
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    @GlobalActionLogout
    lateinit var actionLogout: MutableLiveData<UiEvent>

    @Inject
    @GlobalActionDeferredIssuingEvent
    lateinit var deferredIssuingEvent: MutableSharedFlow<UiDataEvent<DeferredIssuingEvent>?>

    private val vm by viewModels<MainActivityVM>()
    private var deferredResultReceiver: BroadcastReceiver? = null

    private var initialIntentProcessed = false

    private val navHost by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
    }

    private val navController by lazy {
        navHost.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val installSplashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        setUpEdgeToEdge()

        installSplashScreen.setKeepOnScreenCondition {
            vm.showSplash.value
        }

        setContentView(R.layout.activity_main)

        actionLogout.observeUiEvent(this, vm::doLogout)

        vm.checkPinCount()

        collectFlow(vm.navigation) { navigation ->
            val navGraph = navController.navInflater.inflate(R.navigation.nav_main)

            navGraph.setStartDestination(navigation.destinationId)

            navController.graph = navGraph

            navController.navigate(
                navigation.destinationId,
                navigation.bundle,
                NavOptions.Builder()
                    .setPopUpTo(R.id.nav_main, inclusive = true)
                    .setLaunchSingleTop(true)
                    .build()
            )

            if (!initialIntentProcessed) {
                processIntent(
                    intent = this.intent,
                    fromNewIntent = false
                )
                initialIntentProcessed = true
            }

            navHost.view?.doOnPreDraw {
                vm.hideSplash()
            }
        }

        vm.restartApp.observeUiEvent(
            owner = this@MainActivity,
            onChanged = {
                vm.startAuthFlow()
            }
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        overrideConfiguration(newBase)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processIntent(
            intent = intent,
            fromNewIntent = true
        )
    }

    override fun onStart() {
        super.onStart()
        registerDeferredIssuingResultReceiver()
    }
    override fun onStop() {
        unregisterDeferredIssuingResultReceiver()
        super.onStop()
    }


    private fun processIntent(intent: Intent?, fromNewIntent: Boolean) {
        val rawData = intent?.data?.toString() ?: return
        val path = intent.data?.path
        val isSpecialEwalletLink = DeepLinkRoutes.isWalletIntent(rawData)
        val isRegularDeepLink = DeepLinkRoutes.isRegularAppLink(path)
        val needToNavigateToHome = !rawData.contains(DeepLinkRoutes.CREDENTIAL_OFFER_RESUME)
        val shouldHandle = isSpecialEwalletLink || isRegularDeepLink
        if (!shouldHandle) return
        val payload = if (isSpecialEwalletLink) rawData else path ?: return
        if (vm.allowAuthorizedDeepLinks && needToNavigateToHome) {
            navController.navigate(
                R.id.nav_home,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.nav_main, inclusive = true)
                    .setLaunchSingleTop(true)
                    .build()
            )
        }
        vm.processIntentPath(payload, fromNewIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            WorkManager.getInstance(this).cancelUniqueWork("checkVersionWork")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerDeferredIssuingResultReceiver() {
        if (deferredResultReceiver != null) return
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action != DEFERRED_ISSUING_RESULT_ACTION) return
                val status = intent.getStringExtra(DEFERRED_ISSUING_RESULT_STATUS_EXTRA) ?: return
                if (status == DEFERRED_ISSUING_STATUS_NOT_READY) return
                val error = intent.getStringExtra(DEFERRED_ISSUING_RESULT_ERROR_EXTRA)
                deferredIssuingEvent.tryEmit(UiDataEvent(
                    DeferredIssuingEvent(
                        status = status,
                        error = error
                    )
                ))
            }
        }
        ContextCompat.registerReceiver(
            this,
            receiver,
            IntentFilter(DEFERRED_ISSUING_RESULT_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        deferredResultReceiver = receiver
    }

    private fun unregisterDeferredIssuingResultReceiver() {
        deferredResultReceiver?.let { receiver ->
            runCatching { unregisterReceiver(receiver) }
        }
        deferredResultReceiver = null
    }
}

private const val DEFERRED_ISSUING_RESULT_ACTION = "deferred.issuing.result.action"
private const val DEFERRED_ISSUING_RESULT_STATUS_EXTRA = "deferred.issuing.result.status.extra"
private const val DEFERRED_ISSUING_RESULT_ERROR_EXTRA = "deferred.issuing.result.error.extra"
private const val DEFERRED_ISSUING_STATUS_NOT_READY = "not_ready"
