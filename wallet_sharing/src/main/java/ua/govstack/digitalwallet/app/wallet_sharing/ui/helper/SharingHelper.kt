package ua.govstack.digitalwallet.app.wallet_sharing.ui.helper

import androidx.compose.runtime.snapshots.SnapshotStateList
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TextLabelAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TextLabelAtmMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.addAllFromListIfNotNull
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.TableItemCheckboxMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableMainHeadingMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.checkbox.CheckboxCascadeGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.checkbox.CheckboxCascadeOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.BackgroundWhiteOrgData
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_sharing.R
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.humanizeIdentifier
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimPathDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ExpandableListItemUi
import ua.govstack.digitalwallet.app.wallet_core.documents.model.RequestDocumentItemUi
import ua.govstack.digitalwallet.app.wallet_core.documents.util.DocumentJsonKeys

object SharingHelper {

    fun toggleItem(item: ExpandableListItemUi, clickedId: String): ExpandableListItemUi {
        return when (item) {
            is ExpandableListItemUi.SingleListItem -> {
                if (item.header.headerId == clickedId) {
                    item.copy(header = item.header.copy(isSelected = !item.header.isSelected))
                } else item
            }

            is ExpandableListItemUi.NestedListItem -> {
                if (item.header.headerId == clickedId) {
                    val newSelectedState = !item.header.isSelected
                    item.copy(
                        header = item.header.copy(isSelected = newSelectedState),
                        nestedItems = item.nestedItems.map { setSelectionRecursively(it, newSelectedState) }.toMutableList()
                    )
                } else {
                    val updatedChildren = item.nestedItems.map { toggleItem(it, clickedId) }.toMutableList()

                    val anySelected = updatedChildren.any { child ->
                        when (child) {
                            is ExpandableListItemUi.SingleListItem -> child.header.isSelected
                            is ExpandableListItemUi.NestedListItem -> child.header.isSelected
                        }
                    }

                    item.copy(
                        header = item.header.copy(isSelected = anySelected),
                        nestedItems = updatedChildren
                    )
                }
            }
        }
    }

    fun setSelectionRecursively(item: ExpandableListItemUi, selected: Boolean): ExpandableListItemUi {
        return when (item) {
            is ExpandableListItemUi.SingleListItem -> item.copy(header = item.header.copy(isSelected = selected))
            is ExpandableListItemUi.NestedListItem -> item.copy(
                header = item.header.copy(isSelected = selected),
                nestedItems = item.nestedItems.map { setSelectionRecursively(it, selected) }.toMutableList()
            )
        }
    }

    fun domainDataToBackgroundWhiteOrgData(
        source: List<RequestDocumentItemUi?>,
        resourceProvider: ResourceProvider
    ): List<BackgroundWhiteOrgData> {
        val uiElementDataListToDisplay = mutableListOf<BackgroundWhiteOrgData>()

        source.groupBy { it?.domainPayload?.docId }.forEach { documentClaims ->
            val uiDocData = mutableListOf<UIElementData>()
            //Calculate heading for document
            val docTitle = TableMainHeadingMlcData(
                title = documentClaims.value.first()?.domainPayload?.docName.toDynamicString()
            )
            uiDocData.add(docTitle)
            documentClaims.value.filterNotNull().forEach { claim ->
                uiDocData.add(claimToUiRepresentation(claim, resourceProvider))
            }
            val wrapper = BackgroundWhiteOrgData(
                componentId = documentClaims.key.toDynamicString(),
                paddingTop = TopPaddingMode.LARGE,
                paddingHorizontal = SidePaddingMode.MEDIUM,
                items = SnapshotStateList<UIElementData>().apply {
                    addAllFromListIfNotNull(uiDocData)
                }
            )
            uiElementDataListToDisplay.add(wrapper)
        }
        return uiElementDataListToDisplay
    }

    private fun getLabelForIdentifier(
        identifier: String,
        resourceProvider: ResourceProvider
    ): String {
        val translations = SharingDataIdentifier.toIdentifier(identifier)?.getDisplayName(resourceProvider)
        val result = translations ?: identifier.humanizeIdentifier()
        return result
    }

    private fun claimToUiRepresentation(claim: RequestDocumentItemUi, resourceProvider: ResourceProvider): CheckboxCascadeGroupOrgData {
        return CheckboxCascadeGroupOrgData(
            componentId = claim.headerUi.header.headerId,
            mandatory = false,
            items = claim.headerUi.nestedItems.map { expandableListItemUi ->
                expandableListItemUiToUIItem(expandableListItemUi, resourceProvider)
            },
            minMandatorySelectedItems = 0
        )
    }

    private fun expandableListItemUiToUIItem(
        domain: ExpandableListItemUi,
        resourceProvider: ResourceProvider
    ): CheckboxCascadeOrgData {

        fun getLabel(headerValue: String) = getLabelForIdentifier(
            identifier = headerValue,
            resourceProvider = resourceProvider
        ).toDynamicString()

        val parentRows = when (domain) {
            is ExpandableListItemUi.SingleListItem -> listOf(
                TextLabelAtmData(
                    componentId = domain.header.headerId.toDynamicString(),
                    mode = TextLabelAtmMode.PRIMARY,
                    label = getLabel(ClaimPathDomain.toElementIdentifier(domain.header.headerId)),
                    value = if (ClaimPathDomain.toElementIdentifier(domain.header.headerId) in DocumentJsonKeys.BASE64_IMAGE_KEYS) null else domain.header.value.toDynamicString(),
                    isEnabled = true
                )
            )

            is ExpandableListItemUi.NestedListItem -> {
                val valueString = collectValues(domain)
                    .joinToString(", ")
                    .takeIf { it.isNotEmpty() }
                    ?.toDynamicString()

                listOf(
                    TextLabelAtmData(
                        componentId = domain.header.headerId.toDynamicString(),
                        mode = TextLabelAtmMode.PRIMARY,
                        label = getLabel(domain.header.value),
                        value = valueString,
                        isEnabled = true
                    )
                )
            }
        }

        val parentTableItem = TableItemCheckboxMlcData(
            componentId = domain.header.headerId.toDynamicString(),
            mandatory = false,
            rows = parentRows,
            interactionState = UIState.Interaction.Enabled,
            selectionState = if (domain.header.isSelected) UIState.Selection.Selected else UIState.Selection.Unselected
        )

        return CheckboxCascadeOrgData(
            componentId = domain.header.headerId,
            mandatory = false,
            tableItemCheckboxMlcData = parentTableItem,
            isEnabled = true,
            minMandatorySelectedItems = 0,
            paddingTop = TopPaddingMode.NONE
        )
    }

    private fun collectValues(
        item: ExpandableListItemUi
    ): List<String> {
        return when (item) {
            is ExpandableListItemUi.SingleListItem -> {
                if (ClaimPathDomain.toElementIdentifier(item.header.headerId) in DocumentJsonKeys.BASE64_IMAGE_KEYS) {
                    emptyList()
                } else {
                    listOf(item.header.value)
                }
            }

            is ExpandableListItemUi.NestedListItem -> {
                item.nestedItems.flatMap { collectValues(it) }
            }
        }
    }

    enum class SharingDataIdentifier(
        val keys: List<String>,
        val displayNameRes: Int
    ) {

        FAMILY_NAME(
            listOf(
                "family_name",
                "registered_family_name"
            ),
            R.string.sharing_family_name
        ),

        GIVEN_NAME(
            listOf(
                "given_name",
                "registered_given_name"
            ),
            R.string.sharing_given_name
        ),

        FAMILY_NAME_BIRTH(
            listOf(
                "family_name_birth",
                "birth_family_name"
            ),
            R.string.sharing_family_name_birth
        ),

        GIVEN_NAME_BIRTH(
            listOf(
                "given_name_birth",
                "birth_given_name"
            ),
            R.string.sharing_given_name_birth
        ),

        BIRTH_DATE(
            listOf(
                "birth_date",
                "birthdate"
            ),
            R.string.sharing_birth_date
        ),

        AGE_OVER_18(
            listOf("age_over_18"),
            R.string.sharing_age_over_18
        ),

        RESIDENT_ADDRESS(
            listOf(
                "resident_address",
                "residence_address",
                "address"
            ),
            R.string.sharing_resident_address
        ),

        GENDER(
            listOf(
                "gender",
                "sex"
            ),
            R.string.sharing_gender
        ),

        BIRTH_PLACE(
            listOf(
                "birth_place",
                "place_of_birth"
            ),
            R.string.sharing_birth_place
        ),

        NATIONALITY(
            listOf(
                "nationality",
                "nationalities"
            ),
            R.string.sharing_nationality
        ),

        SIGNATURE(
            listOf(
                "signature",
                "signature_usual_mark"
            ),
            R.string.sharing_signature
        ),

        PORTRAIT(
            listOf(
                "portrait",
                "picture"
            ),
            R.string.sharing_portrait
        ),

        ISSUANCE_DATE(
            listOf(
                "issuance_date",
                "date_of_issuance",
                "issue_date"
            ),
            R.string.sharing_issuance_date
        ),

        EXPIRY_DATE(
            listOf(
                "expiry_date",
                "date_of_expiry"
            ),
            R.string.sharing_expiry_date
        ),

        ISSUING_AUTHORITY(
            listOf("issuing_authority"),
            R.string.sharing_issuing_authority
        ),

        ISSUING_COUNTRY(
            listOf(
                "issuing_country",
                "issuing_jurisdiction"
            ),
            R.string.sharing_issuing_country
        ),

        DOCUMENT_NUMBER(
            listOf("document_number"),
            R.string.sharing_document_number
        ),

        UN_DISTINGUISHING_SIGN(
            listOf("un_distinguishing_sign"),
            R.string.sharing_un_distinguishing_sign
        ),

        NATIONALITY_FOR_DISPLAY(
            listOf("nationality_for_display"),
            R.string.sharing_nationality_for_display
        ),

        GENDER_FOR_DISPLAY(
            listOf("gender_for_display"),
            R.string.sharing_gender_for_display
        );

        fun getDisplayName(resourceProvider: ResourceProvider): String {
            return resourceProvider.getString(displayNameRes)
        }

        companion object {
            fun toIdentifier(key: String) = entries.firstOrNull { it.keys.contains(key) }
        }
    }
}