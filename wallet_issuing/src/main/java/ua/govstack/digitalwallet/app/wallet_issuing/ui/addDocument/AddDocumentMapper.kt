package ua.govstack.digitalwallet.app.wallet_issuing.ui.addDocument

import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonIconCircledLargeAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonStrokeAdditionalAtomData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.IconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.GreyTitleAtmData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaginationMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StubInfoMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.TextBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_issuing.R
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.GetAddDocumentOptionsResult
import ua.govstack.digitalwallet.app.wallet_issuing.util.IssuingConsts
import java.net.URI
import javax.inject.Inject

internal class AddDocumentMapper @Inject constructor(
    private val resourceProvider: ResourceProvider,
) {
    fun toolbar(): UIElementData {
        return NavigationPanelMlcV2Data(
            title = resourceProvider
                .getString(R.string.add_document_screen_add_a_document)
                .toDynamicString()
        )
    }

    fun body(
        documentsState: GetAddDocumentOptionsResult,
        isNetworkAvailable: Boolean,
    ): List<UIElementData> {
        val result = mutableListOf<UIElementData>()
        result += TitleLabelMlcData(
            label = resourceProvider.getString(R.string.add_document_screen_add_a_document)
        )
        result += TextBlockOrgData(
            text = null,
            title = UiText.DynamicString(
                resourceProvider.getString(R.string.add_document_screen_choose_a_digital_document)
            ),
            paddingTop = TopPaddingMode.LARGE
        )

        if (!isNetworkAvailable) {
            result += PaginationMessageMlcData(
                id = "",
                title = UiText.StringResource(R.string.no_internet_title),
                description = UiText.StringResource(R.string.no_internet_description),
                button = ButtonStrokeAdditionalAtomData(
                    componentId = IssuingConsts.NO_INTERNET_TRY_AGAIN.toDynamicString(),
                    id = IssuingConsts.NO_INTERNET_TRY_AGAIN,
                    title = UiText.StringResource(R.string.common_try_again),
                    interactionState = UIState.Interaction.Enabled,
                    action = DataActionWrapper(type = IssuingConsts.NO_INTERNET_TRY_AGAIN)
                )
            )
            return result

        }


        when (documentsState) {
            is GetAddDocumentOptionsResult.Success -> {
                result += GreyTitleAtmData(
                    label = UiText.DynamicString(
                        resourceProvider.getString(R.string.add_document_screen_choose_from_list)
                    ),
                    paddingTop = TopPaddingMode.LARGE
                )
                result += documentsState.options.map {
                    CardMlcV2Data(
                        id = it.configurationId,
                        action = DataActionWrapper(
                            subresource = it.credentialIssuerId,
                            resource = it.configurationId,
                            type = IssuingConsts.ADD_DOCUMENT_ISSUANCE_ACTION,
                        ),
                        label = UiText.DynamicString(it.name),
                        descriptions = listOf("", formatIssuerDescription(it.credentialIssuerId)),
                        smallIconAtm = SmallIconAtmData(code = DiiaResourceIcon.ARROW_FILL.code)
                    )
                }
                result += ButtonIconCircledLargeAtmData(
                    actionKey = IssuingConsts.ADD_DOCUMENT_QR_ACTION,
                    icon = UiText.StringResource(R.drawable.ic_qr_scan)
                )
            }

            is GetAddDocumentOptionsResult.NoOptions -> {
                result += StubInfoMessageMlcData(
                    title = UiText.DynamicString(
                        resourceProvider.getString(R.string.add_document_screen_no_new_documents_available)
                    ),
                    icon = IconAtmData(code = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code),
                    text = UiText.DynamicString(
                        resourceProvider.getString(R.string.add_document_screen_you_have_already_added)
                    )
                )
            }

            is GetAddDocumentOptionsResult.Failure -> {
                // Failure state is handled by VM via template dialog.
            }
        }

        return result
    }

    private fun formatIssuerDescription(issuerId: String): String {
        val host = runCatching { URI(issuerId).host }.getOrNull() ?: issuerId
        return resourceProvider.getString(R.string.add_document_screen_issuer_s, host)
    }
}
