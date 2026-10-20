package ua.govstack.digitalwallet.app.diia_storage.store.datasource

import kotlinx.coroutines.flow.Flow

interface DataSource<T> {

    val isDataLoading: Flow<Boolean>

    val data: Flow<DataSourceDataResult<T>>

    fun invalidate()
}
