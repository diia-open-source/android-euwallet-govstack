package ua.govstack.digitalwallet.app.wallet_gallery.domain.model

import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.R
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.DocumentWithMetadata
import ua.govstack.digitalwallet.app.core.models.document.LocalizationType
import ua.govstack.digitalwallet.app.core.models.document.SourceType
import ua.govstack.digitalwallet.app.core.network.Http
import java.util.UUID

@Parcelize
data class DocManualOptions(
    val empty: String = DocName.DOCUMENT_MANUAL_OPTIONS,
    val message: String = "",
    override val id: String? = UUID.randomUUID().toString()
) : Document {

    override fun docId() = id ?: ""
    override fun getItemType() = DocName.DOCUMENT_MANUAL_OPTIONS
    override fun getDocExpirationDate(): String = DEF
    override fun getExpirationDateISO(): String = DEF
    override fun getStatus() = Http.HTTP_200
    override fun getWeight() = Int.MAX_VALUE
    override fun getDocOrder() = DocumentWithMetadata.LAST_DOC_ORDER
    override fun setNewOrder(newOrder: Int) {
    }

    override fun getSourceType() = SourceType.STATIC

    override fun getDocColor() = R.color.colorPrimary

    override fun getDocStackTitle() = ""
    override fun getDocNum() = id
    override fun makeCopy(): Document = this.copy()
    override fun verificationCodesCount() = 1
    override fun getPersonName(): String = ""
    override fun getDisplayDate(): String = ""
    override fun birthCertificateId() = ""
    override fun localization(): LocalizationType = LocalizationType.ua
    override fun setLocalization(code: LocalizationType) {}
    override fun getDocName() = ""
    override fun getDocOrderDescription() = ""
    override fun getDocOrderLabel() = ""

    companion object {
        const val DEF = "PREF_DEF"
    }
}