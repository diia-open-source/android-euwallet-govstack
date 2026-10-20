package ua.govstack.digitalwallet.app.opensource.data.repository.cleaner

interface DataRepositoryCleaner {

    suspend fun clean()
}