package ua.govstack.digitalwallet.app.wallet_sharing.di

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ua.govstack.digitalwallet.app.core.models.document.DocJsonAdapterDelegate
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.DocumentWithMetadata
import ua.govstack.digitalwallet.app.wallet_sharing.domain.serializer.UiSerializer
import ua.govstack.digitalwallet.app.wallet_sharing.domain.serializer.UiSerializerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SerializingAdaptersModule {

    @Provides
    @Singleton
    fun provideDocumentJsonAdapter(docDelegates: List<@JvmSuppressWildcards DocJsonAdapterDelegate<out Document>>): JsonAdapter<List<DocumentWithMetadata>> {
        val moshi = Moshi.Builder().add(
            PolymorphicJsonAdapterFactory.of(Document::class.java, "__type")
                .let {
                    return@let registerJsonAdapters(it, docDelegates)
                }
        )
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter(
            Types.newParameterizedType(
                MutableList::class.java,
                DocumentWithMetadata::class.java
            )
        )
    }


    private fun registerJsonAdapters(
        factory: PolymorphicJsonAdapterFactory<Document>,
        delegates: List<DocJsonAdapterDelegate<out Document>>
    ): PolymorphicJsonAdapterFactory<Document> {
        return if (delegates.isEmpty()) factory
        else {
            val delegate = delegates.first()
            val result = factory.withSubtype(delegate.subtype, delegate.label)
            val newList = delegates.filter { it != delegate }
            registerJsonAdapters(result, newList)
        }
    }

    @Provides
    @Singleton
    fun provideUiSerializer(): UiSerializer = UiSerializerImpl()
}