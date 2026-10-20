package ua.govstack.digitalwallet.app.core.models.document.docgroups

import com.squareup.moshi.Json
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.DocumentGroup
import ua.govstack.digitalwallet.app.core.models.document.DocumentWithMetadata.Companion.LAST_DOC_ORDER
import ua.govstack.digitalwallet.app.core.models.document.Preferences
import ua.govstack.digitalwallet.app.core.models.document.WithETag

abstract class BaseDocumentGroup<T : Document>(
    @property:Json(name = "status")
    var status: Int? = null,
    @property:Json(name = "expirationDate")
    var expirationDate: String = Preferences.DEF,
    @property:Json(name = "currentDate")
    var currentDate: String = Preferences.DEF,
    @property:Json(name = "order")
    var order: Int = LAST_DOC_ORDER,
    @property:Json(name = "eTag")
    @get:JvmName("getDocumentETag") @set:JvmName("setDocumentETag") var eTag: String? = null
) : DocumentGroup<T>, WithETag {

    override fun getStatus(): Int = status ?: 0

    override fun getTimestamp() = currentDate

    override fun getDocExpirationDate() = expirationDate

    override fun getDocOrder() = order

    override fun setNewOrder(newOrder: Int) {
        order = newOrder
    }

    override fun getETag(): String? {
        return eTag
    }
}
