package ua.govstack.digitalwallet.app.wallet_gallery.ui.gallery

import androidx.compose.runtime.snapshots.SnapshotStateList
import ua.govstack.digitalwallet.app.core.models.document.barcode.DocumentBarcodeResultLoading
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnStrokeDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocActivateCardOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocsCarouselItem
import ua.govstack.digitalwallet.app.ui_base.mappers.document.DocumentComposeMapper
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst
import ua.govstack.digitalwallet.app.wallet_gallery.R
import ua.govstack.digitalwallet.app.wallet_gallery.domain.interactor.GetDocumentResult
import javax.inject.Inject

class GalleryMapper @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val composeMapper: DocumentComposeMapper
) {

    fun mapToBody(
        result: GetDocumentResult,
        currPosProvider: () -> Int?
    ): List<UIElementData> {
        return when (result) {
                is GetDocumentResult.Success -> {
                    val carousel = composeMapper.toDocCarousel(
                        result.documents,
                        barcodeResult = DocumentBarcodeResultLoading(loading = false)
                    ).copy(focusOnDoc = currPosProvider())

                    listOf(carousel)
                }

                is GetDocumentResult.Empty -> {
                    listOf(activationCard())
                }
            }
    }


    private fun activationCard() = DocCarouselOrgData(
        data = SnapshotStateList<DocsCarouselItem>().apply {
            add(
                DocActivateCardOrgData(
                    actionKey = WalletConst.TO_ADD_DOCUMENT_ACTION,
                    imageRes = R.drawable.activation_card,
                    title = resourceProvider.getString(R.string.activation_card_title).toDynamicString(),
                    description = resourceProvider.getString(R.string.activation_card_description).toDynamicString(),
                    docType = "",
                    position = 0,
                    primaryBtn = BtnStrokeDefaultAtmData(
                        title = resourceProvider.getString(R.string.activation_card_btn).toDynamicString(),
                        id = "",
                        interactionState = UIState.Interaction.Enabled
                    ),
                )
            )
        },
        focusOnDoc = 0
    )
}