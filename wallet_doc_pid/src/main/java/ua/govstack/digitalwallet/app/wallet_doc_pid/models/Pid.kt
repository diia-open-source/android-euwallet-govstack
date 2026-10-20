package ua.govstack.digitalwallet.app.wallet_doc_pid.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.chip.ChipStatusAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.atm.text.TickerAtm
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.list.ListItemMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.text.SubtitleLabelMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocButtonHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.doc.DocHeadingOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockPlaneOrg.TableBlockPlaneOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.table.tableBlockTwoColumnsPlaneOrg.TableBlockTwoColumnsPlaneOrg
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.DocumentWithMetadata
import ua.govstack.digitalwallet.app.core.models.document.LocalizationType
import ua.govstack.digitalwallet.app.core.models.document.Preferences
import ua.govstack.digitalwallet.app.core.models.document.SourceType
import ua.govstack.digitalwallet.app.core.models.document.WithFrontCard
import ua.govstack.digitalwallet.app.core.models.document.WithListItemGroupOrgPreview
import ua.govstack.digitalwallet.app.core.models.document.WithQrCode
import ua.govstack.digitalwallet.app.core.models.document.docgroups.BaseDocumentGroup
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.DataForDisplayingInOrderConfigurations
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.FrontCard
import ua.govstack.digitalwallet.app.core.models.document.docgroups.v2.FullInfo
import ua.govstack.digitalwallet.app.wallet_doc_pid.R

@Parcelize
@JsonClass(generateAdapter = true)
class Pid(
    @Json(name = "data")
    internal val data: List<Data> = listOf(),
) : BaseDocumentGroup<Pid.Data>() {

    override fun getItemType() = DocName.PID

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "dataForDisplayingInOrderConfigurations")
        val dataForDisplayingInOrderConfigurations: DataForDisplayingInOrderConfigurations?,
        @Json(name = "content")
        val content: List<Content>?,
        @Json(name = "docData")
        val docData: DocData?,
        @Json(name = "docNumber")
        val docNumber: String?,
        @Json(name = "docStatus")
        val docStatus: Int,
        @Json(name = "frontCard")
        val frontCard: FrontCard?,
        @Json(name = "fullInfo")
        val fullInfo: List<FullInfo>?,
        @Json(name = "id")
        override val id: String,
        @Json(name = "localization")
        internal var localization: LocalizationType?,
        @Json(name = "shareLocalization")
        internal var shareLocalization: LocalizationType?,
        @Json(name = "expirationDate")
        internal val expirationDate: String = "",
        @Json(name = "order")
        internal var order: Int = DocumentWithMetadata.LAST_DOC_ORDER,
        @Json(name = "dataForDisplayingAsListItem")
        override var dataForDisplayingAsListItem: ListItemMlc?
    ) : Document, WithFrontCard, WithListItemGroupOrgPreview, WithQrCode {

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class DocData(
            @Json(name = "documentCbor")
            val documentCbor: String?,
            @Json(name = "docName")
            val docName: String?,
            @Json(name = "fullName")
            val fullName: String?
        ) : Parcelable

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Content(
            @Json(name = "image")
            val image: String?,
            @Json(name = "code")
            val code: String?,
        ) : Parcelable

        @IgnoredOnParcel
        val signature = content?.find { it.code == "signature" }

        @IgnoredOnParcel
        val photo = content?.find { it.code == "photo" }?.image

        override fun docId() = id ?: ""
        override fun getItemType() = this.docId()
        override fun getDocColor() = R.color.blue_dark
        override fun getDocStackTitle(): String = ""

        override fun getDocNum() = docNumber
        override fun getStatus() = docStatus
        override fun getExpirationDateISO() = Preferences.DEF
        override fun getWeight() = Int.MAX_VALUE

        override fun makeCopy() = this.copy()
        override fun verificationCodesCount() = 2
        override fun getPersonName(): String {
            return docData?.fullName ?: ""
        }

        override fun getDisplayDate(): String {
            return ""
        }

        override fun getDocOrder() = order
        override fun setNewOrder(newOrder: Int) {
            order = newOrder
        }

        override fun getSourceType(): SourceType = SourceType.DYNAMIC

        override fun birthCertificateId() = ""
        override fun localization(): LocalizationType? = localization
        override fun setLocalization(code: LocalizationType) {
            localization = code
        }

        override fun getDocExpirationDate() = expirationDate

        override fun getDocName() = docData?.docName

        override fun getDocOrderDescription() = dataForDisplayingInOrderConfigurations?.description
        override fun getDocOrderLabel() = this.dataForDisplayingInOrderConfigurations?.label

        override fun getTicker(): TickerAtm? {
            return when (this.localization) {
                LocalizationType.ua -> this.frontCard?.ua?.find {
                    it.tickerAtm != null
                }?.tickerAtm

                LocalizationType.eng -> this.frontCard?.en?.find {
                    it.tickerAtm != null
                }?.tickerAtm

                null -> {
                    when (shareLocalization) {
                        LocalizationType.ua -> this.frontCard?.ua?.find {
                            it.tickerAtm != null
                        }?.tickerAtm

                        LocalizationType.eng -> this.frontCard?.en?.find {
                            it.tickerAtm != null
                        }?.tickerAtm

                        else -> {
                            this.frontCard?.ua?.find {
                                it.tickerAtm != null
                            }?.tickerAtm
                        }
                    }
                }
            }
        }

        override fun getDocHeading(): DocHeadingOrg? {
            return when (this.localization) {
                LocalizationType.ua -> this.frontCard?.ua?.find {
                    it.docHeadingOrg != null
                }?.docHeadingOrg

                LocalizationType.eng -> this.frontCard?.en?.find {
                    it.docHeadingOrg != null
                }?.docHeadingOrg

                null -> {
                    when (shareLocalization) {
                        LocalizationType.ua -> this.frontCard?.ua?.find {
                            it.docHeadingOrg != null
                        }?.docHeadingOrg

                        LocalizationType.eng -> this.frontCard?.en?.find {
                            it.docHeadingOrg != null
                        }?.docHeadingOrg

                        else -> {
                            this.frontCard?.ua?.find {
                                it.docHeadingOrg != null
                            }?.docHeadingOrg
                        }
                    }
                }
            }
        }

        override fun getDocButtonHeading(): DocButtonHeadingOrg? {
            return when (this.localization) {
                LocalizationType.ua -> this.frontCard?.ua?.find {
                    it.docButtonHeadingOrg != null
                }?.docButtonHeadingOrg

                LocalizationType.eng -> this.frontCard?.en?.find {
                    it.docButtonHeadingOrg != null
                }?.docButtonHeadingOrg

                null -> {
                    when (shareLocalization) {
                        LocalizationType.ua -> this.frontCard?.ua?.find {
                            it.docButtonHeadingOrg != null
                        }?.docButtonHeadingOrg

                        LocalizationType.eng -> this.frontCard?.en?.find {
                            it.docButtonHeadingOrg != null
                        }?.docButtonHeadingOrg

                        else -> {
                            this.frontCard?.ua?.find {
                                it.docButtonHeadingOrg != null
                            }?.docButtonHeadingOrg
                        }
                    }
                }
            }
        }

        override fun getTableBlockTwoColumnsPlane(): List<TableBlockTwoColumnsPlaneOrg>? {
            return when (this.localization) {
                LocalizationType.ua -> this.frontCard?.ua?.mapNotNull { it.tableBlockTwoColumnsPlaneOrg }
                LocalizationType.eng -> this.frontCard?.en?.mapNotNull { it.tableBlockTwoColumnsPlaneOrg }
                null -> {
                    when (shareLocalization) {
                        LocalizationType.ua -> this.frontCard?.ua?.mapNotNull { it.tableBlockTwoColumnsPlaneOrg }
                        LocalizationType.eng -> this.frontCard?.en?.mapNotNull { it.tableBlockTwoColumnsPlaneOrg }
                        else -> {
                            this.frontCard?.ua?.mapNotNull { it.tableBlockTwoColumnsPlaneOrg }

                        }
                    }
                }
            }
        }

        override fun getTableBlockPlaneOrg(): List<TableBlockPlaneOrg>? {
            return when (this.localization) {
                LocalizationType.ua -> this.frontCard?.ua?.mapNotNull { it.tableBlockPlaneOrg }
                LocalizationType.eng -> this.frontCard?.en?.mapNotNull { it.tableBlockPlaneOrg }
                null -> {
                    when (shareLocalization) {
                        LocalizationType.ua -> this.frontCard?.ua?.mapNotNull { it.tableBlockPlaneOrg }
                        LocalizationType.eng -> this.frontCard?.en?.mapNotNull { it.tableBlockPlaneOrg }
                        else -> {
                            this.frontCard?.ua?.mapNotNull { it.tableBlockPlaneOrg }

                        }
                    }
                }
            }
        }

        override fun getSubtitleLabel(): SubtitleLabelMlc? {
            return when (this.localization) {
                LocalizationType.ua -> this.frontCard?.ua?.find {
                    it.subtitleLabelMlc != null
                }?.subtitleLabelMlc

                LocalizationType.eng -> this.frontCard?.en?.find {
                    it.subtitleLabelMlc != null
                }?.subtitleLabelMlc

                null -> {
                    when (shareLocalization) {
                        LocalizationType.ua -> this.frontCard?.ua?.find {
                            it.subtitleLabelMlc != null
                        }?.subtitleLabelMlc

                        LocalizationType.eng -> this.frontCard?.en?.find {
                            it.subtitleLabelMlc != null
                        }?.subtitleLabelMlc

                        else -> {
                            this.frontCard?.ua?.find {
                                it.subtitleLabelMlc != null
                            }?.subtitleLabelMlc
                        }
                    }
                }
            }
        }

        override fun getChipStatus(): ChipStatusAtm? {
            return when (this.localization) {
                LocalizationType.ua -> this.frontCard?.ua?.find {
                    it.chipStatusAtm != null
                }?.chipStatusAtm

                LocalizationType.eng -> this.frontCard?.en?.find {
                    it.chipStatusAtm != null
                }?.chipStatusAtm

                null -> {
                    when (shareLocalization) {
                        LocalizationType.ua -> this.frontCard?.ua?.find {
                            it.chipStatusAtm != null
                        }?.chipStatusAtm

                        LocalizationType.eng -> this.frontCard?.en?.find {
                            it.chipStatusAtm != null
                        }?.chipStatusAtm

                        else -> {
                            this.frontCard?.ua?.find {
                                it.chipStatusAtm != null
                            }?.chipStatusAtm
                        }
                    }
                }
            }
        }

        override fun getListItemDocPreview(): ListItemMlc? = dataForDisplayingAsListItem

        override fun setItemPreview(itemPreview: ListItemMlc) {
            dataForDisplayingAsListItem = itemPreview
        }

        override val qrCode: String?
            get() = null

        override val qr: String?
            get() = null
    }

    override fun getData(): List<Data> = data
}