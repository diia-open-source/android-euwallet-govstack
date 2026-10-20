package ua.govstack.digitalwallet.app.ui_base.components.molecule.input

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.govstack.digitalwallet.app.core.models.common_compose.mlc.input.InputNumberMlc
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.SmallIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.icon.toUiModel
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.SidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.TopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toDp
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toSidePaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.toTopPaddingMode
import ua.govstack.digitalwallet.app.ui_base.components.noRippleClickable
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader.GraySpinnerLoaderSubAtm
import ua.govstack.digitalwallet.app.ui_base.components.theme.AzureMist
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha30
import ua.govstack.digitalwallet.app.ui_base.components.theme.BlackAlpha54
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.Grey
import ua.govstack.digitalwallet.app.ui_base.components.theme.PeriwinkleGray
import ua.govstack.digitalwallet.app.ui_base.components.theme.Red
import ua.govstack.digitalwallet.app.ui_base.components.theme.White

private const val FINAL_STATE_CALLBACK = "final_state_callback"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputNumberMlc(
    modifier: Modifier = Modifier,
    data: InputNumberMlcData,
    loader: Loader = Loader.create(),
    withLoadingState: Boolean = data.withLoadingState ?: false,
    imeAction: ImeAction = ImeAction.Default,
    localFocusManager: FocusManager = LocalFocusManager.current,
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    onUIAction: (UIAction) -> Unit
) {
    val isLoading =
        remember { mutableStateOf(loader.isLoadingByComponent(UIActionKeysCompose.INPUT_NUMBER_MLC)) }

    LaunchedEffect(loader) {
        isLoading.value = loader.isLoadingByComponent(data.actionKey)
    }

    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(data.finalState) {
        if (data.finalState) {
            val result = alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
            if (result.endReason == AnimationEndReason.Finished) {
                onUIAction(
                    UIAction(
                        actionKey = FINAL_STATE_CALLBACK,
                    )
                )
            }
        } else {
            alphaAnim.snapTo(0f)
        }
    }

    var focusState by remember { mutableStateOf<UIState.Focus>(UIState.Focus.NeverBeenFocused) }

    val bringIntoInputViewRequester = BringIntoViewRequester()
    val bringIntoHintViewRequester = BringIntoViewRequester()
    val bringIntoErrorViewRequester = BringIntoViewRequester()

    val maskSymbolsCount: Int = data.mask?.count { c -> c == '#' } ?: (Int.MAX_VALUE - 1)

    if (WindowInsets.isImeVisible) {
        LaunchedEffect(Unit) {
            if (data.validationState == UIState.Validation.Failed) {
                bringIntoErrorViewRequester.bringIntoView()
            } else if (data.validationState != UIState.Validation.Failed && data.hint != null) {
                bringIntoHintViewRequester.bringIntoView()
            } else {
                bringIntoInputViewRequester.bringIntoView()
            }
        }
    }

    BasicTextField(
        modifier = modifier
            .padding(
                start = data.sidePaddingMode.toDp(defaultPadding = 16.dp),
                top = data.topPaddingMode.toDp(defaultPadding = 8.dp),
                end = data.sidePaddingMode.toDp(defaultPadding = 16.dp)
            )
            .onFocusChanged { newFocusState ->
                focusState = when (focusState) {
                    UIState.Focus.NeverBeenFocused -> {
                        if (data.value == null) {
                            if (newFocusState.isFocused || newFocusState.hasFocus) {
                                UIState.Focus.FirstTimeInFocus
                            } else {
                                UIState.Focus.NeverBeenFocused
                            }
                        } else {
                            UIState.Focus.OutOfFocus
                        }
                    }

                    UIState.Focus.FirstTimeInFocus -> UIState.Focus.OutOfFocus

                    UIState.Focus.InFocus -> UIState.Focus.OutOfFocus

                    UIState.Focus.OutOfFocus -> UIState.Focus.InFocus
                }
            }
            .bringIntoViewRequester(bringIntoInputViewRequester),
        value = data.value.orEmpty(),
        enabled = data.interactionState == UIState.Interaction.Enabled,
        onValueChange = { newValue ->
            if (
                data.interactionState == UIState.Interaction.Enabled &&
                newValue.length < maskSymbolsCount + 1
            ) {
                onUIAction(
                    UIAction(
                        actionKey = data.actionKey,
                        action = DataActionWrapper(
                            type = data.actionKey,
                            resource = data.componentId,
                            subresource = newValue
                        )
                    )
                )
            }
        },
        textStyle = DiiaTextStyle.t1BigText.copy(
            color = getInputColor(
                interactionState = data.interactionState
            )
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                localFocusManager.moveFocus(FocusDirection.Next)
            },
            onDone = {
                keyboardController?.let {
                    localFocusManager.clearFocus()
                    it.hide()
                }
            }
        ),
        singleLine = true,
        cursorBrush = SolidColor(Black),
        visualTransformation = data.mask?.let { mask ->
            MaskVisualTransformation(
                mask = mask,
                maskSymbol = '#'
            )
        } ?: VisualTransformation.None,
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = getBackgroundColor(
                                interactionState = data.interactionState
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = getBorderColor(
                                focusState = focusState,
                                validationState = data.validationState,
                                interactionState = data.interactionState
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                ) {
                    Row {
                        Column(
                            modifier = Modifier
                                .weight(1F),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                style = DiiaTextStyle.t4TextSmallDescription,
                                text = data.label,
                                color = getLabelColor(
                                    focusState = focusState,
                                    validationState = data.validationState,
                                    interactionState = data.interactionState
                                ),
                                maxLines = 1
                            )
                            val inputFieldHeight = with(LocalDensity.current) {
                                DiiaTextStyle.t1BigText.lineHeight.toDp()
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(inputFieldHeight),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (data.value == null && data.placeholder != null) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.Center),
                                        style = DiiaTextStyle.t1BigText,
                                        text = data.placeholder,
                                        color = BlackAlpha30,
                                        maxLines = 1
                                    )
                                }
                                innerTextField()
                            }
                        }

                        if (!withLoadingState) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .noRippleClickable {
                                        data.iconRight?.let {
                                            onUIAction(it.action())
                                        }
                                    }
                            ) {
                                if (data.iconRight != null && data.value == null) {
                                    SmallIconAtm(
                                        data = data.iconRight,
                                        onUIAction = onUIAction
                                    )
                                } else if (data.iconRight != null && !focusState.isFocused()) {
                                    SmallIconAtm(
                                        data = data.iconRight,
                                        onUIAction = onUIAction
                                    )
                                } else if (focusState.isFocused() && !data.value.isNullOrEmpty()) {
                                    Image(
                                        modifier = modifier
                                            .size(24.dp)
                                            .noRippleClickable {
                                                onUIAction(
                                                    UIAction(
                                                        actionKey = UIActionKeysCompose.CLEAR_INPUT,
                                                        action = DataActionWrapper(
                                                            type = data.actionKey,
                                                            resource = data.componentId
                                                        )
                                                    )
                                                )
                                            },
                                        painter = painterResource(
                                            id = DiiaResourceIcon.getResourceId(DiiaResourceIcon.CLEAR_INPUT.code)
                                        ),
                                        contentDescription = null
                                    )
                                }
                            }
                        } else {
                            if (isLoading.value) {
                                GraySpinnerLoaderSubAtm(
                                    modifier = Modifier
                                        .padding(7.dp)
                                        .size(18.dp)
                                )
                            } else {
                                if (focusState.isFocused() && data.value != null) {
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                    ) {
                                        Image(
                                            modifier = modifier
                                                .size(24.dp)
                                                .noRippleClickable {
                                                    onUIAction(
                                                        UIAction(
                                                            actionKey = UIActionKeysCompose.CLEAR_INPUT,
                                                            action = DataActionWrapper(
                                                                type = data.actionKey,
                                                                resource = data.componentId
                                                            )
                                                        )
                                                    )
                                                },
                                            painter = painterResource(
                                                id = DiiaResourceIcon.getResourceId(DiiaResourceIcon.CLEAR_INPUT.code)
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                } else if (data.finalState || alphaAnim.value > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .alpha(alphaAnim.value)
                                    ) {
                                        Image(
                                            modifier = modifier.size(24.dp),
                                            painter = painterResource(id = R.drawable.ic_done),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (data.validationState != UIState.Validation.Failed) {
                    data.hint?.let { lHint ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .padding(start = 16.dp)
                                .bringIntoViewRequester(bringIntoHintViewRequester),
                            style = DiiaTextStyle.t4TextSmallDescription,
                            text = lHint,
                            color = BlackAlpha54
                        )
                    }
                }

                if (data.validationState == UIState.Validation.Failed) {
                    data.errorMessage?.let { lErrorMessage ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .padding(start = 16.dp)
                                .bringIntoViewRequester(bringIntoErrorViewRequester),
                            style = DiiaTextStyle.t4TextSmallDescription,
                            text = lErrorMessage,
                            color = Red
                        )
                    }
                }
            }
        }
    )
}

private fun getBackgroundColor(
    interactionState: UIState.Interaction
): Color {
    return if (interactionState == UIState.Interaction.Enabled) {
        White
    } else {
        AzureMist
    }
}

private fun getBorderColor(
    focusState: UIState.Focus,
    validationState: UIState.Validation,
    interactionState: UIState.Interaction
): Color {
    return when (focusState) {
        UIState.Focus.NeverBeenFocused -> {
            if (interactionState == UIState.Interaction.Enabled) {
                PeriwinkleGray
            } else {
                AzureMist
            }
        }

        UIState.Focus.FirstTimeInFocus -> {
            if (validationState == UIState.Validation.Failed) {
                Red
            } else {
                Black
            }
        }

        UIState.Focus.InFocus -> {
            if (validationState == UIState.Validation.Failed) {
                Red
            } else {
                Black
            }
        }

        UIState.Focus.OutOfFocus -> {
            if (validationState == UIState.Validation.Failed) {
                Red
            } else {
                PeriwinkleGray
            }
        }
    }
}

private fun getLabelColor(
    focusState: UIState.Focus,
    validationState: UIState.Validation,
    interactionState: UIState.Interaction
): Color {
    return when (focusState) {
        UIState.Focus.NeverBeenFocused -> {
            if (interactionState == UIState.Interaction.Enabled) {
                Black
            } else {
                BlackAlpha30
            }
        }

        UIState.Focus.FirstTimeInFocus -> {
            if (validationState == UIState.Validation.Failed) {
                Red
            } else {
                Black
            }
        }

        UIState.Focus.InFocus -> {
            if (validationState == UIState.Validation.Failed) {
                Red
            } else {
                Black
            }
        }

        UIState.Focus.OutOfFocus -> {
            if (validationState == UIState.Validation.Failed) {
                Red
            } else {
                Black
            }
        }
    }
}

private fun getInputColor(
    interactionState: UIState.Interaction
): Color {
    return if (interactionState == UIState.Interaction.Enabled) {
        Black
    } else {
        BlackAlpha30
    }
}

private class MaskVisualTransformation(
    val mask: String,
    val maskSymbol: Char
) : VisualTransformation {

    private val maxLength = mask.count { it == maskSymbol }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskSymbol) {
                    val nextDigitIndex = mask.indexOf(maskSymbol, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        return TransformedText(
            text = annotatedString,
            offsetMapping = MaskOffsetMapper(mask, maskSymbol)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneVisualTransformation) return false
        if (mask != other.mask) return false
        if (maskSymbol != other.maskSymbol) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

class MaskOffsetMapper(val mask: String, val maskSymbol: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            if (mask[i++] != maskSymbol) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != maskSymbol }

}

data class InputNumberMlcData(
    val actionKey: String = UIActionKeysCompose.INPUT_NUMBER_MLC,
    val componentId: String? = null,
    val inputCode: String? = null,
    val label: String,
    val placeholder: String? = null,
    val hint: String? = null,
    val mask: String? = null,
    val validationItems: List<ValidationItem>? = null,
    val value: String? = null,
    val minValue: Long? = null,
    val maxValue: Long? = null,
    val withLoadingState: Boolean? = null,
    val finalState: Boolean = false,
    val mandatory: Boolean? = null,
    var errorMessage: String? = null,
    val iconRight: SmallIconAtmData? = null,
    val topPaddingMode: TopPaddingMode? = null,
    val sidePaddingMode: SidePaddingMode? = null,
    val validationState: UIState.Validation = UIState.Validation.NeverBeenPerformed,
    val interactionState: UIState.Interaction = UIState.Interaction.Enabled
) : UIElementData {

    data class ValidationItem(
        val regex: String,
        val flags: List<String>,
        val errorMessage: String
    )

    fun showSuccessState(state: Boolean): InputNumberMlcData {
        return copy(
            finalState = state
        )
    }

    fun onInputChanged(newValue: String?): InputNumberMlcData {
        val updatedValue = if (newValue.isNullOrEmpty()) null else newValue
        return copy(
            value = updatedValue,
            validationState = validateData(updatedValue)
        )
    }

    fun clearInput(): InputNumberMlcData {
        return copy(
            value = null,
            validationState = UIState.Validation.NeverBeenPerformed
        )
    }

}

private fun InputNumberMlcData.validateData(newValue: String?): UIState.Validation {
    var isMatchesWithRegex: Boolean? = null

    if (newValue.isNullOrEmpty()) {
        return UIState.Validation.Passed
    }

    if (minValue != null && newValue.toLong() < minValue) {
        return UIState.Validation.Failed
    }

    if (maxValue != null && newValue.toLong() > maxValue) {
        return UIState.Validation.Failed
    }

    if (validationItems.isNullOrEmpty()) {
        return UIState.Validation.Passed
    }

    for (validation in validationItems) {
        if (newValue.matches(Regex(validation.regex))) {
            isMatchesWithRegex = true
        } else {
            isMatchesWithRegex = false
            errorMessage = validation.errorMessage
            break
        }
    }

    if (isMatchesWithRegex != null && !isMatchesWithRegex) return UIState.Validation.Failed

    return UIState.Validation.Passed
}

fun InputNumberMlc.toUIModel() = InputNumberMlcData(
    componentId = componentId,
    inputCode = inputCode,
    label = label,
    placeholder = placeholder,
    hint = hint,
    mask = mask,
    validationItems = validation?.map { validationItem ->
        InputNumberMlcData.ValidationItem(
            regex = validationItem.regexp,
            flags = validationItem.flags,
            errorMessage = validationItem.errorMessage
        )
    },
    value = value,
    minValue = minValue,
    maxValue = maxValue,
    mandatory = mandatory,
    errorMessage = errorMessage,
    iconRight = iconRight?.toUiModel(),
    topPaddingMode = paddingMode?.top?.toTopPaddingMode(),
    sidePaddingMode = paddingMode?.side?.toSidePaddingMode(),
    interactionState = if (isDisabled == true) UIState.Interaction.Disabled else UIState.Interaction.Enabled
)

fun generateMockInputNumberMlcData(
    id: Int? = null,
    value: String? = null,
    hint: String? = null,
    withLoadingState: Boolean? = null
) = InputNumberMlcData(
    actionKey = if (id != null) "input_number_mlc_component_id_$id" else UIActionKeysCompose.INPUT_NUMBER_MLC,
    componentId = "input_number_mlc_component_id_$id",
    inputCode = "input_number_mlc_input_code_$id",
    label = "Label $id",
    placeholder = "Placeholder $id",
    hint = hint ?: "Hint $id",
    mask = "### ## #",
    value = value,
    withLoadingState = withLoadingState ?: false,
    finalState = true,
    mandatory = null,
    errorMessage = "Error message $id",
    iconRight = SmallIconAtmData(
        code = DiiaResourceIcon.BARCODE_SCAN.code
    ),
    sidePaddingMode = SidePaddingMode.NONE
)

@Preview
@Composable
fun InputNumberMlcPreview() {
    var inputNumberMlcData by remember {
        mutableStateOf(generateMockInputNumberMlcData())
    }

    var inputNumberMlcWithValueData by remember {
        mutableStateOf(generateMockInputNumberMlcData(id = 1, value = "03578"))
    }

    var inputNumberMlcLoadingData by remember {
        mutableStateOf(generateMockInputNumberMlcData(2, value = null, withLoadingState = true))
    }

    var inputNumberMlcLoadingFinalStateData by remember {
        mutableStateOf(generateMockInputNumberMlcData(3, value = "03578", withLoadingState = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Grey)
            .statusBarsPadding()
    ) {
        InputNumberMlc(
            data = inputNumberMlcData,
            onUIAction = { uiAction ->
                when (uiAction.actionKey) {
                    UIActionKeysCompose.INPUT_NUMBER_MLC -> {
                        inputNumberMlcData = inputNumberMlcData.onInputChanged(
                            newValue = uiAction.action?.subresource
                        )
                    }

                    UIActionKeysCompose.CLEAR_INPUT -> {
                        inputNumberMlcData = inputNumberMlcData.clearInput()
                    }
                }
            }
        )

        InputNumberMlc(
            data = inputNumberMlcWithValueData,
            onUIAction = { uiAction ->
                when (uiAction.actionKey) {
                    UIActionKeysCompose.INPUT_NUMBER_MLC -> {
                        inputNumberMlcWithValueData = inputNumberMlcWithValueData.onInputChanged(
                            newValue = uiAction.action?.subresource
                        )
                    }

                    UIActionKeysCompose.CLEAR_INPUT -> {
                        inputNumberMlcWithValueData = inputNumberMlcWithValueData.clearInput()
                    }
                }
            }
        )

        InputNumberMlc(
            data = inputNumberMlcLoadingData,
            loader = Loader.createComponent(
                key = "input_number_mlc_component_id_2",
                isLoading = true
            ),
            onUIAction = { uiAction ->
                when (uiAction.actionKey) {
                    UIActionKeysCompose.INPUT_NUMBER_MLC -> {
                        inputNumberMlcLoadingData = inputNumberMlcLoadingData.onInputChanged(
                            newValue = uiAction.action?.subresource
                        )
                    }

                    UIActionKeysCompose.CLEAR_INPUT -> {
                        inputNumberMlcLoadingData = inputNumberMlcLoadingData.clearInput()
                    }
                }
            }
        )

        InputNumberMlc(
            data = inputNumberMlcLoadingFinalStateData,
            loader = Loader.createComponent("input_number_mlc_component_id_3", isLoading = false),
            onUIAction = { uiAction ->
                when (uiAction.actionKey) {
                    UIActionKeysCompose.INPUT_NUMBER_MLC -> {
                        inputNumberMlcLoadingFinalStateData =
                            inputNumberMlcLoadingFinalStateData.onInputChanged(
                                newValue = uiAction.action?.subresource
                            )
                    }
                }
            }
        )
    }
}