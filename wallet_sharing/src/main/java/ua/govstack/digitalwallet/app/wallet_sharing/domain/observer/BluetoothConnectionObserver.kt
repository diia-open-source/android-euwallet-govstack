package ua.govstack.digitalwallet.app.wallet_sharing.domain.observer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

interface BluetoothConnectionObserver {
    fun observeBluetoothState(): Flow<Boolean>
    fun isBluetoothEnabled(): Boolean
}

@Singleton
class BluetoothConnectionObserverImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : BluetoothConnectionObserver {

    override fun observeBluetoothState(): Flow<Boolean> = callbackFlow {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        trySend(bluetoothManager?.adapter?.isEnabled == true)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    trySend(state == BluetoothAdapter.STATE_ON)
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()

    override fun isBluetoothEnabled(): Boolean {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        return bluetoothManager?.adapter?.isEnabled == true
    }
}
