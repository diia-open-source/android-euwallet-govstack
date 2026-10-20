package ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.checkbox.CheckboxSquareMlc
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextParameter
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextParameter.Data
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextWithParametersAtom
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.textwithparameter.TextWithParametersData
import ua.govstack.digitalwallet.app.ui_base.components.conditional
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicStringOrNull
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha30
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.Transparent
import ua.govstack.digitalwallet.app.ui_base.components.theme.White

@Composable
fun CheckboxSquareMlc(
    modifier: Modifier = Modifier,
    data: CheckboxSquareMlcData,
    onUIAction: (UIAction) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .conditional(data.usePadding) {
                padding(horizontal = 24.dp)
            }
            .conditional(data.interactionState == UIState.Interaction.Enabled) {
                noRippleClickable {
                    onUIAction(
                        UIAction(
                            actionKey = data.actionKey,
                            data = data.id,
                            optionalId = data.options.firstOrNull {
                                val condition = data.selectionState != UIState.Selection.Selected
                                it.isSelected == condition
                            }?.id
                        )
                    )
                }
            }
            .semantics(mergeDescendants = true) {
                role = Role.Checkbox
                selected = data.selectionState == UIState.Selection.Selected
            },
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = when (data.interactionState) {
                        UIState.Interaction.Disabled -> {
                            when (data.selectionState) {
                                UIState.Selection.Selected -> BlackAlpha30
                                UIState.Selection.Unselected -> Transparent
                            }
                        }

                        UIState.Interaction.Enabled -> {
                            when (data.selectionState) {
                                UIState.Selection.Selected -> Black
                                UIState.Selection.Unselected -> Transparent
                            }
                        }
                    }, shape = RoundedCornerShape(4.dp)
                )
                .conditional(data.selectionState == UIState.Selection.Unselected) {
                    border(
                        color = when (data.interactionState) {
                            UIState.Interaction.Disabled -> BlackAlpha30
                            UIState.Interaction.Enabled -> Black
                        }, width = 2.dp, shape = RoundedCornerShape(4.dp)
                    )
                }
                .size(20.dp)
                .testTag(data.componentId?.asString() ?: ""),
            contentAlignment = Alignment.Center
        ) {
            if (data.selectionState == UIState.Selection.Selected) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    painter = painterResource(id = R.drawable.diia_check),
                    contentDescription = null,
                    tint = White
                )
            }
        }

        TextWithParametersAtom(
            modifier = Modifier
                .padding(start = 12.dp, top = 2.dp)
                .alpha(
                    when (data.interactionState) {
                        UIState.Interaction.Disabled -> 0.3f
                        UIState.Interaction.Enabled -> 1f
                    }
                ),
            data = data.title,
            style = DiiaTextStyle.t3TextBody,
            onUIAction = onUIAction
        )

//        Text(
//            modifier = Modifier.padding(start = 12.dp, top = 2.dp),
//            text = data.title.asString(),
//            color = when (data.interactionState) {
//                UIState.Interaction.Disabled -> BlackAlpha30
//                UIState.Interaction.Enabled -> Black
//            },
//            style = DiiaTextStyle.t3TextBody
//        )
    }
}

data class CheckboxSquareMlcData(
    val componentId: UiText? = null,
    val actionKey: String = UIActionKeysCompose.CHECKBOX_REGULAR,
    val id: String = "",
//    val title: UiText,
    val title: TextWithParametersData,
    val contentDescription: UiText? = null,
    val interactionState: UIState.Interaction,
    val selectionState: UIState.Selection,
    val options: List<Option> = listOf(),
    val usePadding: Boolean = false
) : UIElementData, Cloneable {

    public override fun clone(): CheckboxSquareMlcData {
        return super.clone() as CheckboxSquareMlcData
    }

    fun onCheckboxClick(): CheckboxSquareMlcData {
        return this.copy(selectionState = this.selectionState.reverse())
    }

    fun getCurrentOption(): Option? {
        return options.firstOrNull {
            val condition = selectionState == UIState.Selection.Selected
            it.isSelected == condition
        }
    }

    data class Option(
        val id: String,
        val isSelected: Boolean
    )
}

fun String?.toUIModel(
    actionKey: String = UIActionKeysCompose.CHECKBOX_REGULAR,
    id: String = "",
    interactionState: UIState.Interaction = UIState.Interaction.Enabled,
    selectionState: UIState.Selection = UIState.Selection.Unselected
): CheckboxSquareMlcData? {
    if (this == null) return null
    return CheckboxSquareMlcData(
        actionKey = actionKey,
        id = id,
        title = TextWithParametersData(
            text = UiText.DynamicString(this),
            parameters = emptyList()
        ),
        interactionState = interactionState,
        selectionState = selectionState
    )
}

fun CheckboxSquareMlc?.toUIModel(): CheckboxSquareMlcData? {
    val entity: CheckboxSquareMlc = this ?: return null
    val options = mutableListOf<CheckboxSquareMlcData.Option>()
    this.options?.forEach { item ->
        options.add(
            CheckboxSquareMlcData.Option(
                id = item.id,
                isSelected = item.isSelected
            )
        )
    }
    return CheckboxSquareMlcData(
        actionKey = UIActionKeysCompose.CHECKBOX_REGULAR,
        id = entity.componentId ?: "",
        title = TextWithParametersData(
            text = UiText.DynamicString(entity.label),
            parameters = if (entity.parameters != null) {
                mutableListOf<TextParameter>().apply {
                    entity.parameters?.forEach {
                        add(
                            TextParameter(
                                data = TextParameter.Data(
                                    name = it.data?.name.toDynamicStringOrNull(),
                                    resource = it.data?.resource.toDynamicStringOrNull(),
                                    alt = it.data?.alt.toDynamicStringOrNull()
                                ),
                                type = it.type
                            )
                        )
                    }
                }

            } else emptyList()
        ),
        options = options,
        interactionState = this.isEnabled?.let { state ->
            if (state) UIState.Interaction.Enabled else UIState.Interaction.Disabled
        } ?: UIState.Interaction.Enabled,
        selectionState = this.isSelected?.let { state ->
            if (!state) UIState.Selection.Unselected else UIState.Selection.Selected
        } ?: UIState.Selection.Unselected,
    )
}

@Composable
@Preview
fun CheckboxSquareAtomPreview_EnabledState_Checked() {
    val data = CheckboxSquareMlcData(
        title = TextWithParametersData(
            text = UiText.DynamicString("Label")
        ),
        id = "",
        interactionState = UIState.Interaction.Enabled,
        selectionState = UIState.Selection.Selected
    )
    val state = remember { mutableStateOf(data) }

    CheckboxSquareMlc(data = state.value) {
        state.value = state.value.onCheckboxClick()
    }
}

@Composable
@Preview
fun CheckboxSquareAtomPreview_EnabledState_Unchecked() {
    val data = CheckboxSquareMlcData(
        title = TextWithParametersData(
            text = UiText.DynamicString("Label")
        ),
        id = "",
        interactionState = UIState.Interaction.Enabled,
        selectionState = UIState.Selection.Unselected
    )
    val state = remember { mutableStateOf(data) }
    CheckboxSquareMlc(data = state.value) {
        state.value = state.value.onCheckboxClick()
    }
}

@Composable
@Preview
fun CheckboxSquareAtomPreview_DisabledState_Checked() {
    val data = CheckboxSquareMlcData(
        title = TextWithParametersData(
            text = UiText.DynamicString("Label")
        ),
        id = "",
        interactionState = UIState.Interaction.Disabled,
        selectionState = UIState.Selection.Selected
    )
    val state = remember { mutableStateOf(data) }
    CheckboxSquareMlc(data = state.value) {
        state.value = state.value.onCheckboxClick()
    }
}

@Composable
@Preview
fun CheckboxSquareAtomPreview_DisabledState_Unchecked() {
    val data = CheckboxSquareMlcData(
        title = TextWithParametersData(
            text = UiText.DynamicString("Label")
        ),
        id = "",
        interactionState = UIState.Interaction.Disabled,
        selectionState = UIState.Selection.Unselected
    )
    val state = remember { mutableStateOf(data) }
    CheckboxSquareMlc(data = state.value) {
        state.value = state.value.onCheckboxClick()
    }
}

@Composable
@Preview
fun CheckboxSquareAtomPreview_WithParam_Checked() {
    val data = CheckboxSquareMlcData(
        title = TextWithParametersData(
            text = UiText.DynamicString("Довгий текст, довгий текст. \\\nДалі іде текст з лінкою і в resource передається послилання: {link2}."),
            parameters = listOf(
                TextParameter(
                    type = "link",
                    data = Data(
                        name = "link2".toDynamicStringOrNull(),
                        alt = "текст з лінкою".toDynamicStringOrNull(),
                        resource = "https://appeal.mva.gov.ua/".toDynamicStringOrNull()
                    )
                )
            )
        ),
        id = "",
        interactionState = UIState.Interaction.Enabled,
        selectionState = UIState.Selection.Selected
    )
    val state = remember { mutableStateOf(data) }

    CheckboxSquareMlc(data = state.value) {
        state.value = state.value.onCheckboxClick()
    }
}