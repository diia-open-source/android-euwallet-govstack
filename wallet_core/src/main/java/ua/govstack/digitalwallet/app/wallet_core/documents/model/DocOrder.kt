package ua.govstack.digitalwallet.app.wallet_core.documents.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

interface DocumentOrderModel

@JsonClass(generateAdapter = true)
data class DocOrder(
    @param:Json(name = "documentType")
    val documentType: String,
    @param:Json(name = "order")
    val order: Int
): DocumentOrderModel

@JsonClass(generateAdapter = true)
data class TypeDefinedDocOrder(
    @param:Json(name = "docNumber")
    val docNumber: String,
    @param:Json(name = "order")
    val order: Int
): DocumentOrderModel