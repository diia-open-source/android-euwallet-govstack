package ua.govstack.digitalwallet.app.wallet_core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.wallet_core.database.EWalletDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideEWalletDatabase(@ApplicationContext context: Context): EWalletDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            EWalletDatabase::class.java,
            "EWalletDatabase.db"
        )
            .fallbackToDestructiveMigration(true).build()

    @Provides
    @Singleton
    fun provideRevokedDocumentDao(db: EWalletDatabase) = db.revokedDocumentDao()

    @Provides
    @Singleton
    fun provideTransactionLogDao(db: EWalletDatabase) = db.transactionLogDao()
}