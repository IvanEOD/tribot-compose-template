package scripts.kt.gui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import scripts.kt.gui.ScriptText
import scripts.kt.gui.theme.ScriptColorProvider
import scripts.kt.gui.theme.ScriptSurface


/* Written by IvanEOD 9/21/2022, at 3:43 PM */



interface TextFieldConverter<T> {
    fun convert(text: String): T
    fun convert(value: T): String
}

private fun <T> defaultConverter(fromString: (String) -> T) = object : TextFieldConverter<T> {
    override fun convert(text: String): T = fromString(text)
    override fun convert(value: T): String = value.toString()
}

@Composable
fun LabeledNumberInput(
    label: String,
    labelWidth: Dp = 100.dp,
    inputWidth: Dp? = null,
    startPadding: Dp = 0.dp,
    textValue: MutableState<Int>,
    converter: TextFieldConverter<Int> = defaultConverter {
        if (it.isEmpty()) 0 else it.toInt()
    },
    textStyle: TextStyle = MaterialTheme.typography.subtitle1,
    onValueChange: (Int) -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    placeholder: String? = null,
    placeholderStyle: TextStyle = textStyle,
) = AbstractLabeledTextField(
    label,
    labelWidth,
    inputWidth,
    startPadding,
    textValue,
    converter,
    textStyle,
    onValueChange,
    true,
    keyboardActions,
    keyboardOptions,
    trailingIcon,
    leadingIcon,
    modifier,
    enabled,
    singleLine,
    maxLines,
    placeholder,
    placeholderStyle
)

@Composable
fun LabeledTextInput(
    label: String,
    labelWidth: Dp = 100.dp,
    inputWidth: Dp? = null,
    startPadding: Dp = 0.dp,
    textValue: MutableState<String>,
    converter: TextFieldConverter<String> = defaultConverter { it },
    textStyle: TextStyle = MaterialTheme.typography.subtitle1,
    onValueChange: (String) -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Default
    ),
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    placeholder: String? = null,
    placeholderStyle: TextStyle = textStyle,
) = AbstractLabeledTextField(
    label,
    labelWidth,
    inputWidth,
    startPadding,
    textValue,
    converter,
    textStyle,
    onValueChange,
    false,
    keyboardActions,
    keyboardOptions,
    trailingIcon,
    leadingIcon,
    modifier,
    enabled,
    singleLine,
    maxLines,
    placeholder,
    placeholderStyle
)


@Composable
private fun <T> AbstractLabeledTextField(
    label: String,
    labelWidth: Dp = 100.dp,
    inputWidth: Dp? = null,
    startPadding: Dp = 0.dp,
    textValue: MutableState<T>,
    converter: TextFieldConverter<T> = defaultConverter { it as T },
    textStyle: TextStyle = MaterialTheme.typography.subtitle1,
    onValueChange: (T) -> Unit = {},
    isNumbersOnly: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    placeholder: String? = null,
    placeholderStyle: TextStyle = textStyle,
) {

    Row(
        modifier = Modifier.fillMaxSize().padding(start = startPadding),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.width(labelWidth).height(TextFieldDefaults.MinHeight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) { ScriptText(text = label, fontAlignment = TextAlign.Start, modifier = Modifier.fillMaxWidth()) }
        Column(
            modifier = Modifier.fillMaxWidth().height(TextFieldDefaults.MinHeight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ScriptSurface(modifier =
                if (inputWidth != null)
                    Modifier.width(inputWidth).border(
                        border = ScriptColorProvider.colors.textFieldBorder,
                        shape = ScriptColorProvider.colors.textFieldShape)
                else Modifier.fillMaxWidth().border(
                    border = ScriptColorProvider.colors.textFieldBorder,
                    shape = ScriptColorProvider.colors.textFieldShape)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    TextField(
                        modifier = modifier.fillMaxWidth()
                            .height(TextFieldDefaults.MinHeight)
                            .clip(ScriptColorProvider.colors.textFieldShape),
                        value = converter.convert(textValue.value),
                        onValueChange = { query: String ->
                            if (isNumbersOnly) {
                                var cleaned = query.replace("\\D".toRegex(), "")
                                if (cleaned.isEmpty()) cleaned = "0"
                                textValue.value = converter.convert(cleaned)
                            } else {
                                textValue.value = converter.convert(query)
                            }
                            onValueChange(converter.convert(query))
                        },
                        textStyle = textStyle,
                        placeholder = { placeholder?.let { ScriptText(text = it, style = placeholderStyle) } },
                        maxLines = maxLines,
                        singleLine = singleLine,
                        enabled = enabled,
                        shape = ScriptColorProvider.colors.textFieldShape,
                        trailingIcon = trailingIcon,
                        leadingIcon = leadingIcon,
                        keyboardActions = keyboardActions,
                        keyboardOptions = keyboardOptions,
                    )
                }

            }
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
}