package ua.govstack.digitalwallet.app.wallet_gallery.domain.provider

import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocOrder

interface EUDocOrderProvider {

    suspend fun getOrderByDocType(docType: String): Int

    suspend fun getEUDocOrders(): Map<String, Integer>

    suspend fun saveDocTypeOrder(docOrders: List<DocOrder>)
}
