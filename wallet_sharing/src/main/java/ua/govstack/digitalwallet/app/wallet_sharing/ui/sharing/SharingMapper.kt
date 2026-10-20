package ua.govstack.digitalwallet.app.wallet_sharing.ui.sharing

import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.MiddleIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SlideBarAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SlideBarAtmType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionIconMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.BackgroundMode
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.TextBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentPayloadDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ExpandableListItemUi
import ua.govstack.digitalwallet.app.wallet_core.documents.model.RequestDocumentItemUi
import ua.govstack.digitalwallet.app.wallet_sharing.R
import ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer.RequestTransformer
import ua.govstack.digitalwallet.app.wallet_sharing.ui.helper.SharingHelper
import ua.govstack.digitalwallet.app.wallet_sharing.util.SharingConst
import javax.inject.Inject

class SharingMapper @Inject constructor(
    private val resourceProvider: ResourceProvider,
) {

    fun toolbar(): UIElementData = TopGroupOrgData(
        navigationPanelMlcV2Data = NavigationPanelMlcV2Data(
            title = UiText.StringResource(R.string.sharing_screen_navigation_panel_title),
            isClosed = true,
            iconLeft = MiddleIconAtmData(code = DiiaResourceIcon.BACK.code)
        )
    )

    fun sharingHeader(): UIElementData = TitleLabelMlcData(
        label = resourceProvider.getString(R.string.sharing_screen_header)
    )

    fun infoBlock(): UIElementData = AttentionIconMessageMlcData(
        icon = SmallIconAtmData(code = DiiaResourceIcon.ELLIPSE_INFO.code),
        text = UiText.StringResource(R.string.sharing_info_block),
        backgroundMode = BackgroundMode.NOTE,
        paddingHorizontal = SidePaddingMode.MEDIUM
    )

    fun requesterHeader(verifierName: String?): UIElementData = TextBlockOrgData(
        paddingTop = TopPaddingMode.LARGE,
        text = null,
        title = UiText.StringResource(
            R.string.sharing_screen_requester_label,
            verifierName ?: resourceProvider.getString(R.string.unrecognized_verifier)
        )
    )

    fun bottomGroup(): UIElementData = BottomGroupOrgData(
        primaryButton = BtnPrimaryDefaultAtmData(
            actionKey = SharingConst.BTN_SHARE_DATA_ACTION_KEY,
            id = SharingConst.BTN_SHARE_DATA_ACTION_KEY,
            componentId = SharingConst.BTN_SHARE_DATA_ACTION_KEY.toDynamicString(),
            title = UiText.StringResource(R.string.sharing_screen_btn_share_data),
            interactionState = UIState.Interaction.Enabled
        )
    )

    fun snackBar(): UIElementData = SlideBarAtmData(
        value = UiText.StringResource(R.string.sharing_screen_snack_bar_notification),
        type = SlideBarAtmType.Attention
    )

    fun bottomGroupWithState(current: BottomGroupOrgData, enabled: Boolean): UIElementData =
        current.copy(
            primaryButton = current.primaryButton?.copy(
                interactionState = if (enabled) {
                    UIState.Interaction.Enabled
                } else {
                    UIState.Interaction.Disabled
                }
            )
        )

    fun toRequestDocumentItems(documentsDomain: List<DocumentPayloadDomain>): List<RequestDocumentItemUi> =
        RequestTransformer.transformToUiItems(documentsDomain = documentsDomain)

    fun changedDocument(document: RequestDocumentItemUi, clickedElementId: String) =
        RequestDocumentItemUi(
            domainPayload = document.domainPayload,
            headerUi = ExpandableListItemUi.NestedListItem(
                header = document.headerUi.header,
                nestedItems = document.headerUi.nestedItems.map {
                    SharingHelper.toggleItem(it, clickedElementId)
                },
                isExpanded = true
            )
        )

    fun mapToClaimList(source: List<RequestDocumentItemUi?>) =
        SharingHelper.domainDataToBackgroundWhiteOrgData(source, resourceProvider)
}