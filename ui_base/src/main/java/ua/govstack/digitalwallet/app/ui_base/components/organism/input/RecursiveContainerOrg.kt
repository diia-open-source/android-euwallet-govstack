package ua.govstack.digitalwallet.app.ui_base.components.organism.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import ua.govstack.digitalwallet.app.core.models.common_compose.org.container.BackgroundWhiteOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.container.InputBlockOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.input.RecursiveContainerOrg
import ua.govstack.digitalwallet.app.core.models.common_compose.org.input.RecursiveContainerOrgItem
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnAddOptionAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnAddOptionAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnWhiteLargeIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnWhiteLargeIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.toUIModel
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.IconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.generateMocKSmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtmType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicString
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.toDynamicStringOrNull
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputPhoneCodeOrgV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputTextMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SelectorOrgData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SelectorOrgV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.TextInputMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableMainHeadingMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.BackgroundWhiteOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.BackgroundWhiteOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.ContainerFormItem
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.InputBlockOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.InputBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.generateInputBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.toUIModel
import java.util.UUID
import kotlin.String

@Composable
fun RecursiveContainerOrg(
    modifier: Modifier = Modifier,
    data: RecursiveContainerOrgData,
    onUIAction: (UIAction) -> Unit = {}
) {
    var dataLocal by remember(data) { mutableStateOf(data) }

    var showItems by remember { mutableStateOf(false) }

    LaunchedEffect(data.expandState, data.items.size) {
        showItems = false
        if (data.expandState == UIState.Expand.Expanded && data.items.isNotEmpty()) {
            withFrameNanos { }
            showItems = true
        }
    }

    Column(
        modifier
            .fillMaxWidth()
            .testTag(data.componentId?.asString() ?: "")
    ) {
        AnimatedVisibility(visible = dataLocal.expandState == UIState.Expand.Collapsed) {
            SpacerAtm(data = SpacerAtmData(SpacerAtmType.LARGE))
        }

        AnimatedVisibility(
            visible = showItems,
            enter = expandVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                ),
                expandFrom = Alignment.Top,
            ) + slideInVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                ), initialOffsetY = { -it }),
            exit = slideOutVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                ), targetOffsetY = { -it }) + shrinkVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutLinearInEasing
                ), shrinkTowards = Alignment.Top
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                dataLocal.items.forEachIndexed { index, item ->
                    when (item) {
                        is BackgroundWhiteOrgData -> BackgroundWhiteOrg(
                            modifier = Modifier,
                            data = item
                        ) {
                            onUIAction(
                                UIAction(
                                    actionKey = it.actionKey,
                                    data = "${item.id}\\s${it.optionalId}\\s${it.data}",
                                    action = it.action
                                )
                            )
                        }

                        is InputBlockOrgData -> InputBlockOrg(
                            modifier = Modifier,
                            data = item,
                            onUIAction = { onUIAction(
                                UIAction(
                                    actionKey = it.actionKey,
                                    data = "${item.id}\\s${it.optionalId}\\s${it.data}",
                                    action = it.action
                                )
                            ) }
                        )
                    }
                }
            }
        }

        data.btnWhiteLargeIconAtmData?.let {
            BtnWhiteLargeIconAtm(
                data = it,
                onUIAction = onUIAction
            )
        }
        data.btnAddOptionAtmData?.let {
            BtnAddOptionAtm(
                data = it,
                onUIAction = onUIAction
            )
        }
    }
}

data class RecursiveContainerOrgData(
    val actionKey: String = UIActionKeysCompose.RECURSIVE_CONTAINER_ORG,
    val componentId: UiText? = null,
    val expandState: UIState.Expand = UIState.Expand.Expanded,
    val interactionState: UIState.Interaction = UIState.Interaction.Enabled,
    val items: MutableList<ContainerFormItem>,
    val itemsMaxSize: Int?,
    val btnWhiteLargeIconAtmData: BtnWhiteLargeIconAtmData? = null,
    val btnAddOptionAtmData: BtnAddOptionAtmData? = null,
    val template: ContainerFormItem?

) : UIElementData, Cloneable {

    public override fun clone(): RecursiveContainerOrgData {
        return super.clone() as RecursiveContainerOrgData
    }

    fun changeBtnState(state: UIState.Interaction): RecursiveContainerOrgData {
        return this.copy(
            btnWhiteLargeIconAtmData = this.btnWhiteLargeIconAtmData?.copy(
                interactionState = state
            )
        )
    }

    fun changeAddOptionBtnState(state: UIState.Interaction): RecursiveContainerOrgData {
        return this.copy(
            btnAddOptionAtmData = this.btnAddOptionAtmData?.copy(
                interactionState = state
            )
        )
    }

    fun isFormFilledAndValid(): Boolean {
        val validation = items.all { item ->
            when(item) {
                is BackgroundWhiteOrgData -> {
                    item.isFormFilledAndValid()
                }

                is InputBlockOrgData -> {
                    item.isFormFilledAndValid()
                }
            }
        }
        return validation
    }

    fun expandOrAddItem(): RecursiveContainerOrgData {
        val shouldAddItem = items.isEmpty()
        return when (expandState) {
            UIState.Expand.Collapsed -> {
                if (shouldAddItem && template != null) {
                    val newItem = when(template) {
                        is BackgroundWhiteOrgData -> {
                            template.copy(
                                id = UUID.randomUUID().toString(),
                                items = template.items.map {
                                    when (it) {
                                        is TextInputMoleculeData -> it.copy(
                                            inputValue = "",
                                            validation = UIState.Validation.NeverBeenPerformed
                                        )

                                        is SelectorOrgData -> it.copy(inputValue = "")
                                        else -> it
                                    }
                                }.toMutableStateList()
                            )
                        }

                        is InputBlockOrgData -> {
                            template.copy(
                                id = UUID.randomUUID().toString(),
                                items = template.items.map {
                                    when(it) {
                                        is InputTextMlcV2Data -> it.copy()
                                        is InputPhoneCodeOrgV2Data -> it.copy()
                                        is SelectorOrgV2Data -> it.copy()
                                        else -> it
                                    }
                                }.toMutableStateList()
                            )
                        }
                    }
                    val result = this.copy(
                        expandState = UIState.Expand.Expanded,
                        items = mutableStateListOf(newItem)
                    )
                    return result
                } else {
                    val result = this.copy(expandState = UIState.Expand.Expanded)
                    return result
                }
            }

            UIState.Expand.Expanded -> {
                if (items.size < (itemsMaxSize ?: 10)) {
                    var newItem: ContainerFormItem
                    template?.let { templateItem ->
                        when(templateItem) {
                            is BackgroundWhiteOrgData -> {
                                newItem = templateItem.copy(
                                    id = UUID.randomUUID().toString(),
                                    items = templateItem.items.map {
                                        when (it) {
                                            is TextInputMoleculeData -> it.copy(
                                                inputValue = "",
                                                validation = UIState.Validation.NeverBeenPerformed
                                            )

                                            is SelectorOrgData -> it.copy(inputValue = "")
                                            else -> it
                                        }
                                    }.toMutableStateList()
                                )
                            }

                            is InputBlockOrgData -> {
                                newItem = templateItem.copy(
                                    id = UUID.randomUUID().toString(),
                                    items = templateItem.items.map {
                                        when(it) {
                                            is InputTextMlcV2Data -> it.copy()
                                            is InputPhoneCodeOrgV2Data -> it.copy()
                                            is SelectorOrgV2Data -> it.copy()
                                            else -> it
                                        }
                                    }.toMutableStateList()
                                )
                            }
                        }


                        val updatedItems = items + newItem
                        val result = this.copy(
                            items = updatedItems.toMutableStateList()
                        )
                        return result
                    } ?: this
                } else {
                    val result = this.copy(interactionState = UIState.Interaction.Disabled)
                    return result
                }
            }
        }
    }

    fun onInputChanged(id: String?): RecursiveContainerOrgData {
        if (id.isNullOrEmpty()) return this
        val idList: List<String> = id.split("\\s")
        val newItems = items.map { item ->
            when(item) {
                is BackgroundWhiteOrgData -> {
                    if (item.id == idList[0]) {
                        item.onInputChanged(idList[1], idList[2])
                    } else {
                        item
                    }
                }

                is InputBlockOrgData -> {
                    if (item.id == idList[0]) {
                        item.onInputChanged(idList[1], idList[2])
                    } else {
                        item
                    }
                }
            }
        }
        return this.copy(
            items = SnapshotStateList<ContainerFormItem>().apply {
                addAll(newItems)
            })
    }

    fun onInputChanged(id: String?, newValue: String): RecursiveContainerOrgData {
        if (id.isNullOrEmpty()) return this
        val idList: List<String> = id.split("\\s")
        val newItems = items.map { item ->
            when(item) {
                is BackgroundWhiteOrgData -> {
                    if (item.id == idList[0]) {
                        item.onInputChanged(idList[1], newValue)
                    } else {
                        item
                    }
                }

                is InputBlockOrgData -> {
                    if (item.id == idList[0]) {
                        item.onInputChanged(idList[1], idList[2])
                    } else {
                        item
                    }
                }
            }
        }
        return this.copy(
            items = SnapshotStateList<ContainerFormItem>().apply {
                addAll(newItems)
            })
    }

    fun onClearInput(inputBlockOrgId: String?, fieldId: String?): RecursiveContainerOrgData {
        if (inputBlockOrgId.isNullOrEmpty()) return this
        val idList: List<String> = inputBlockOrgId.split("\\s")
        val newItems = items.map { item ->
            when(item) {
                is BackgroundWhiteOrgData -> {
                    if (item.id == idList[0]) {
                        item.onClearInput(idList[0])
                    } else {
                        item
                    }
                }

                is InputBlockOrgData -> {
                    if (item.id == idList[0]) {
                        item.onClearInput(fieldId)
                    } else {
                        item
                    }
                }
            }
        }
        return this.copy(
            items = SnapshotStateList<ContainerFormItem>().apply {
                addAll(newItems)
            })
    }
    fun removeOrCollapseItem(id: String? = null): RecursiveContainerOrgData {
        if (id.isNullOrEmpty()) return this

        val idList: List<String> = id.split("\\s")
        val updatedItems: MutableList<ContainerFormItem> = items.map { item ->
            when(item) {
                is BackgroundWhiteOrgData -> {
                    if (item.id == idList[0]) {
                        item.copy(
                            items = item.items.map { inputItem ->
                                when (inputItem) {
                                    is TextInputMoleculeData -> inputItem.copy(
                                        inputValue = "",
                                        validation = UIState.Validation.NeverBeenPerformed
                                    )

                                    is SelectorOrgData -> inputItem.copy(
                                        inputValue = "",
                                    )

                                    else -> inputItem
                                }
                            }.toMutableStateList()
                        )
                    } else {
                        item
                    }
                }

                is InputBlockOrgData -> {
                    if (item.id == idList[0]) {
                        item.copy(
                            items = item.items.map { inputItem ->
                                when (inputItem) {
                                    is InputTextMlcV2Data -> inputItem.copy()
                                    is InputPhoneCodeOrgV2Data -> inputItem.copy()
                                    is SelectorOrgV2Data -> inputItem.copy()
                                    else -> inputItem
                                }
                            }.toMutableStateList()
                        )
                    } else {
                        item
                    }
                }
            }

        }.filterNot {
            when(it) {
                is BackgroundWhiteOrgData -> {
                    it.id == idList[0]
                }

                is InputBlockOrgData -> {
                    it.id == idList[0]
                }
            }
        }.toMutableStateList()

        return if (items.size == 1) {
            this.copy(
                expandState = UIState.Expand.Collapsed,
                items = updatedItems,
                interactionState = UIState.Interaction.Enabled
            )
        } else {
            this.copy(
                items = updatedItems,
                interactionState = UIState.Interaction.Enabled
            )
        }
    }
}

fun RecursiveContainerOrg?.toUiModel(): RecursiveContainerOrgData? {
    val entity = this
    if (entity?.items == null) return null
    val items = mutableListOf<ContainerFormItem>().apply {
        (entity.items as List<RecursiveContainerOrgItem>).forEach { item ->
            if (item.backgroundWhiteOrg is BackgroundWhiteOrg) {
                add(
                    (item.backgroundWhiteOrg as BackgroundWhiteOrg).toUIModel()
                )
            }
            if (item.inputBlockOrg is InputBlockOrg) {
                add(
                    (item.inputBlockOrg as InputBlockOrg).toUIModel()
                )
            }
        }
    }
    val template = if (entity.template?.backgroundWhiteOrg != null) {
        entity.template?.backgroundWhiteOrg?.toUIModel()
    } else if(entity.template?.inputBlockOrg != null) {
        entity.template?.inputBlockOrg?.toUIModel()
    } else {
        entity.template?.backgroundWhiteOrg?.toUIModel() //default
    }
    return RecursiveContainerOrgData(
        items = items,
        componentId = entity.componentId.toDynamicStringOrNull(),
        itemsMaxSize = entity.maxNumber,
        btnWhiteLargeIconAtmData = entity.btnWhiteLargeIconAtm?.toUIModel(),
        btnAddOptionAtmData = if (entity.btnAddOptionAtm?.action == null) {
            entity.btnAddOptionAtm?.toUiModel()?.copy(
                action = DataActionWrapper(
                    type = "add_new_template"
                )
            )
        } else {
            entity.btnAddOptionAtm?.toUiModel()
        },
        template = template
    )
}

@Composable
@Preview
fun RecursiveContainerOrg_BackgroundWhiteOrg_Preview() {
    val tableMainHeadingMlc1 = TableMainHeadingMlcData(
        title = "Main title".toDynamicString(),
        iconAtmData = IconAtmData(
            code = "delete"
        )
    )
    val textInputMlc1 = TextInputMoleculeData(
        id = "11K",
        label = LoremIpsum(2).values.joinToString(),
        inputValue = "",
        placeholder = "Placeholder",
        hintMessage = LoremIpsum(5).values.joinToString(),
        validationData = listOf(
            TextInputMoleculeData.ValidationTextItem(
                regex = "[a-zA-Z]",
                flags = listOf(),
                errorMessage = "error",
            )
        ),
        validation = UIState.Validation.NeverBeenPerformed
    )
    val textInputMlc2 = TextInputMoleculeData(
        id = "12B",
        label = LoremIpsum(2).values.joinToString(),
        inputValue = "",
        placeholder = "Placeholder",
        hintMessage = LoremIpsum(5).values.joinToString(),
        validationData = listOf(
            TextInputMoleculeData.ValidationTextItem(
                regex = "[a-zA-Z]",
                flags = listOf(),
                errorMessage = "error",
            )
        ),
        validation = UIState.Validation.NeverBeenPerformed
    )
    val backgroundWhiteOrgData = BackgroundWhiteOrgData(
        items = SnapshotStateList<UIElementData>().apply {
            add(tableMainHeadingMlc1)
            add(textInputMlc1)
            add(textInputMlc2)
        }
    )

    val data = RecursiveContainerOrgData(
        items = SnapshotStateList<ContainerFormItem>().apply {
            add(backgroundWhiteOrgData)
            add(backgroundWhiteOrgData)
        },
        itemsMaxSize = 10,
        template = null,
        btnWhiteLargeIconAtmData = BtnWhiteLargeIconAtmData(label = "Label", id = "")
    )
    val state = remember {
        mutableStateOf(data)
    }
    RecursiveContainerOrg(data = state.value) {
        when (it.actionKey) {
            "inputForm" -> {
                state.value = state.value.onInputChanged(it.data)
            }

            "textInput" -> {
                state.value = state.value.onInputChanged(it.data)
            }

            UIActionKeysCompose.RECURSIVE_CONTAINER_ORG_EXPAND_AND_ADD -> {
                state.value = state.value.expandOrAddItem()
            }

            "actionRemove" -> {
                state.value = state.value.removeOrCollapseItem(it.data)
            }
        }
    }
}

@Composable
@Preview
fun RecursiveContainerOrg_InputBlockOrg_Preview() {
    var inputBlockOrg by remember { mutableStateOf(generateInputBlockOrgData()) }
    val data = RecursiveContainerOrgData(
        items = SnapshotStateList<ContainerFormItem>().apply {
            add(inputBlockOrg)
            add(inputBlockOrg)
        },
        itemsMaxSize = 10,
        template = null,
        btnAddOptionAtmData = BtnAddOptionAtmData(
            componentId = "componentId",
            label = "Label",
            description = "Description",
            iconLeft = generateMocKSmallIconAtmData(
                componentId = "small_icon_atm_componentId",
                diiaResourceIcon = DiiaResourceIcon.ICON_ADD
            ),
            interactionState = UIState.Interaction.Enabled,
            action = DataActionWrapper(
                type = "btn_add_option_atm_action",
                resource = "btn_add_option_atm_resource"
            )
        )
    )
    val state2 = remember { mutableStateOf(data) }
    RecursiveContainerOrg(data = state2.value) {
        when (it.actionKey) {
            "inputForm" -> {
                state2.value = state2.value.onInputChanged(it.data)
            }

            "textInput" -> {
                state2.value = state2.value.onInputChanged(it.data)
            }

            UIActionKeysCompose.RECURSIVE_CONTAINER_ORG_EXPAND_AND_ADD -> {
                state2.value = state2.value.expandOrAddItem()
            }

            "actionRemove" -> {
                state2.value = state2.value.removeOrCollapseItem(it.data)
            }
        }
    }
}