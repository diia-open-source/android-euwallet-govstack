package ua.govstack.digitalwallet.app.wallet_gallery.domain.provider

import ua.govstack.digitalwallet.app.diia_storage.store.datasource.preferences.PreferenceDataSource
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocOrder

class EUDocOrderProviderImpl(
    private val preferenceDataSource: PreferenceDataSource,
) : EUDocOrderProvider {

    companion object {
        const val EU_ORDER_MAP = "eu_order_map"
    }

    override suspend fun getOrderByDocType(docType: String): Int {
        return preferenceDataSource.getMap(EU_ORDER_MAP, Integer::class.java)[docType]?.toInt() ?: 0
    }

    override suspend fun getEUDocOrders(): Map<String, Integer> {
        return preferenceDataSource.getMap(EU_ORDER_MAP, Integer::class.java)
    }

    override suspend fun saveDocTypeOrder(docOrders: List<DocOrder>) {
        val mapToSave = docOrders.associate { it.documentType to it.order }
        preferenceDataSource.saveMap(EU_ORDER_MAP, mapToSave)
    }
}
