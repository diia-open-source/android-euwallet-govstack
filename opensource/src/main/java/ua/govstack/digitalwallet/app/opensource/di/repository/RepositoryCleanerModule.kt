package ua.govstack.digitalwallet.app.opensource.di.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.opensource.data.repository.cleaner.DataRepositoryCleaner
import ua.govstack.digitalwallet.app.opensource.data.repository.cleaner.DataRepositoryMemoryCleaner

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryCleanerModule {

    @Binds
    fun bindRepoCleaner(impl: DataRepositoryMemoryCleaner): DataRepositoryCleaner
}