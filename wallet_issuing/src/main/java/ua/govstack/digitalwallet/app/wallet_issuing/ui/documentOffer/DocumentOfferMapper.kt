package ua.govstack.digitalwallet.app.wallet_issuing.ui.documentOffer

import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst.ACTION_NAVIGATE_BACK
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.MiddleIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.TextBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst.ACCEPT_OFFER
import ua.govstack.digitalwallet.app.wallet_issuing.R
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.DocumentOffer
import ua.govstack.digitalwallet.app.wallet_issuing.util.IssuingConsts
import javax.inject.Inject

internal class DocumentOfferMapper @Inject constructor(
    private val resourceProvider: ResourceProvider,
) {
    fun toolbar(): UIElementData {
        return TopGroupOrgData(
            navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
                title = UiText.StringResource(R.string.credential_offer_screen_toolbar_title),
                isClosed = true,
                iconLeft = MiddleIconAtmData(code = DiiaResourceIcon.BACK.code)
            )
        )
    }

    fun bottom(): UIElementData {
        return BottomGroupOrgData(
            primaryButton = BtnPrimaryDefaultAtmData(
                actionKey = IssuingConsts.ACCEPT_OFFER_BTN,
                componentId = IssuingConsts.ACCEPT_OFFER_BTN.toDynamicString(),
                title = UiText.StringResource(R.string.document_offer_add_document),
                id = IssuingConsts.ACCEPT_OFFER_BTN,
                action = DataActionWrapper(type = ACCEPT_OFFER),
                interactionState = UIState.Interaction.Enabled
            ),
            secondaryButton = BtnPlainAtmData(
                title = UiText.StringResource(R.string.document_offer_add_document_no),
                action = DataActionWrapper(type = ACTION_NAVIGATE_BACK)
            )
        )
    }

    fun body(documents: List<DocumentOffer>): List<UIElementData> {
        if (documents.isEmpty()) return emptyList()

        val title = TitleLabelMlcData(
            label = resourceProvider.getString(R.string.document_issuance_request),
            paddingHorizontal = SidePaddingMode.MEDIUM
        )
        val text = TextBlockOrgData(
            text = null,
            title = UiText.StringResource(R.string.document_offer_permission_text),
        )
        val cards = documents.map { offeredDocument ->
            CardMlcV2Data(
                actionKey = UIActionKeysCompose.CARD_MLC_V2,
                id = "",
                label = UiText.DynamicString(offeredDocument.title),
                descriptions = listOfNotNull(
                    offeredDocument.issuer?.let {
                        resourceProvider.getString(R.string.document_issuer_format, it)
                    }
                ),
                paddingHorizontal = SidePaddingMode.MEDIUM
            )
        }

        return listOf(title, text) + cards
    }
}
