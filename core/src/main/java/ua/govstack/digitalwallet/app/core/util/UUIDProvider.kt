package ua.govstack.digitalwallet.app.core.util

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface UuidProvider {
    fun provideUuid(): String
}

class UuidProviderImpl() : UuidProvider {
    @OptIn(ExperimentalUuidApi::class)
    override fun provideUuid(): String {
        return Uuid.random().toString()
    }
}