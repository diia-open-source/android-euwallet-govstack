package ua.govstack.digitalwallet.app.ui_base.components.organism.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ua.govstack.digitalwallet.app.core.models.common_compose.org.block.AttachmentsOrg
import ua.govstack.digitalwallet.app.core.util.extensions.pop
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnAddOptionAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnAddOptionAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.generateMockBtnAddOptionAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose.ATTACHMENTS_ORG
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.findActionBundleByType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.pushBundle
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.AttachmentCardMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.AttachmentCardMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.generateMockAttachmentCardMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackSqueeze

@Composable
fun AttachmentsOrg(
    modifier: Modifier = Modifier,
    data: AttachmentsOrgData,
    onUIAction: (UIAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        data.items.forEach { attachmentCardMlc ->
            AttachmentCardMlc(
                data = attachmentCardMlc,
                onUIAction = { uIAction ->
                    onUIAction(
                        uIAction.copy(
                            actionKey = data.actionKey
                        ).pushBundle(
                            componentId = data.componentId
                        )
                    )
                }
            )
        }
        if (data.btnAddOptionAtm != null) {
            BtnAddOptionAtm(
                data = data.btnAddOptionAtm,
                onUIAction = { uIAction ->
                    onUIAction(
                        uIAction.copy(
                            actionKey = data.actionKey
                        ).pushBundle(
                            componentId = data.componentId
                        )
                    )
                }
            )
        }
    }
}

data class AttachmentsOrgData(
    val actionKey: String = ATTACHMENTS_ORG,
    val componentId: String? = null,
    val inputCode: String? = null,
    val minNumber: Int? = null,
    val maxNumber: Int? = null,
    val mandatory: Boolean = false,
    val items: List<AttachmentCardMlcData> = emptyList(),
    val btnAddOptionAtm: BtnAddOptionAtmData? = null
) : UIElementData

fun AttachmentsOrg.toUiModel() = AttachmentsOrgData(
    componentId = componentId,
    inputCode = inputCode,
    minNumber = minNumber,
    maxNumber = maxNumber,
    mandatory = mandatory ?: false,
    items = items?.mapNotNull { item -> item.attachmentCardMlc?.toUiModel() }.orEmpty(),
    btnAddOptionAtm = btnAddOptionAtm?.toUiModel()
)

fun generateMockAttachmentsOrgData(
    componentId: String = "attachments_org_0"
) = AttachmentsOrgData(
    componentId = componentId,
    items = listOf(
        generateMockAttachmentCardMlcData()
    ),
    btnAddOptionAtm = generateMockBtnAddOptionAtmData()
)

@Preview
@Composable
fun AttachmentsOrgPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackSqueeze)
            .statusBarsPadding()
    ) {
        AttachmentsOrg(
            data = generateMockAttachmentsOrgData(),
            onUIAction = { uiAction ->
                when (uiAction.actionKey) {
                    ATTACHMENTS_ORG -> {
                        val actionBundle = uiAction.actionBundles.pop()
                        val clickedParentComponentId = actionBundle?.componentId
                        uiAction.findActionBundleByType(
                            type = "small_icon_atm_action"
                        )?.let { actionBundle ->
                            val resource = actionBundle.action?.resource
                            // Clicked item resource: $resource, Parent id: $clickedParentComponentId
                        }
                        uiAction.findActionBundleByType(
                            type = "btn_add_option_atm_action"
                        )?.let { actionBundle ->
                            val resource = actionBundle.action?.resource
                            // Clicked item resource: $resource, Parent id: $clickedParentComponentId
                        }
                    }
                }
            }
        )
    }
}