package ua.govstack.digitalwallet.app.core.network.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    val isAvailable: Boolean

    fun observe(): Flow<Boolean>
}