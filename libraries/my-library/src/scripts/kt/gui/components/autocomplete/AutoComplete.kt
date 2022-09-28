package scripts.kt.gui.components.autocomplete

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import scripts.kt.gui.ScriptDivider
import scripts.kt.gui.ScriptText
import scripts.kt.gui.theme.ScriptColorProvider
import scripts.kt.gui.theme.ScriptSurface


/* Written by IvanEOD 9/16/2022, at 8:16 PM */

@Composable
fun <T> rememberAutoCompleteState(
    listBuilder: ((String) -> List<T>)? = null,
    favoritesListBuilder: ((String) -> List<T>)? = null,
    startItems: List<T> = if (listBuilder != null)
        listBuilder("") else emptyList(),
    favoritesList: MutableState<List<T>> = if (favoritesListBuilder != null)
        mutableStateOf(favoritesListBuilder("")) else mutableStateOf(emptyList()),
    itemToString: (T) -> String = { it.toString() },
) = remember {
    AutoCompleteState(
        listBuilder,
        favoritesListBuilder,
        startItems,
        favoritesList,
        itemToString
    )
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> SimpleAutoComplete(
    label: String,
    labelWidth: Dp = 100.dp,
    inputWidth: Dp? = null,
    autoCompleteState: AutoCompleteState<T>,
    content: @Composable AutoCompleteState<T>.() -> Unit = {},
) {
    AutoComplete(
        label = label,
        labelWidth = labelWidth,
        inputWidth = inputWidth,
        autoCompleteState = autoCompleteState,
        suggestedItemComponent = { AutoCompleteItem(autoCompleteState.itemToString(it.value)) },
        content = content)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> AutoCompleteWithFavorites(
    label: String,
    labelWidth: Dp = 100.dp,
    inputWidth: Dp? = null,
    autoCompleteState: AutoCompleteState<T>,
    content: @Composable AutoCompleteState<T>.() -> Unit = {},
) {
    AutoComplete(label = label,
        labelWidth = labelWidth,
        inputWidth = inputWidth,
        autoCompleteState = autoCompleteState,
        suggestedItemComponent = {
            AutoCompleteItemWithFavorites(autoCompleteState.itemToString(it.value), it.isFavorite)
        },
        content = content)
}


@ExperimentalAnimationApi
@Composable
private fun <T> AutoComplete(
    label: String,
    labelWidth: Dp = 100.dp,
    inputWidth: Dp? = null,
    autoCompleteState: AutoCompleteState<T>,
    suggestedItemComponent: @Composable (AutoCompleteSupplier<T>) -> Unit,
    content: @Composable AutoCompleteState<T>.() -> Unit = {},
) {
    val listState = rememberLazyListState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.width(labelWidth).height(TextFieldDefaults.MinHeight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            ScriptText(
                text = label,
                fontAlignment = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            val (value, setValue) = remember { autoCompleteState.textFieldValue }
            val focusManager = LocalFocusManager.current

            fun onItemSelected(item: T) {
                setValue(autoCompleteState.itemToString(item))
                autoCompleteState.selectItem(item)
                autoCompleteState.filter(value)
                focusManager.clearFocus()
            }

            ScriptSurface(modifier = if (inputWidth != null) Modifier.width(inputWidth)
                .border(border = ScriptColorProvider.colors.textFieldBorder,
                    shape = ScriptColorProvider.colors.textFieldShape)
            else Modifier.fillMaxWidth()
                .border(border = ScriptColorProvider.colors.textFieldBorder,
                    shape = ScriptColorProvider.colors.textFieldShape)) {
                Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                    Box {
                        TextField(
                            modifier = Modifier.fillMaxWidth().height(TextFieldDefaults.MinHeight)
                                .clip(ScriptColorProvider.colors.textFieldShape)
                                .onFocusChanged { focusState -> autoCompleteState.isSearching = focusState.isFocused },
                            value = value,
                            onValueChange = {
                                setValue(it)
                                autoCompleteState.filter(it)
                            },
                            textStyle = MaterialTheme.typography.subtitle1,
                            singleLine = true,
                            shape = ScriptColorProvider.colors.textFieldShape,
                            trailingIcon = {
                                if (value.isNotEmpty()) {
                                    IconButton(onClick = { setValue("") }) {
                                        Icon(imageVector = Icons.Outlined.Clear, contentDescription = null)
                                    }
                                }
                            },
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }, onSend = {
                                    focusManager.clearFocus()
                                }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text)
                        )
                    }

                    AnimatedVisibility(visible = autoCompleteState.isSearching) {
                        Box {
                            Column(Modifier.fillMaxWidth()) {
                                ScriptDivider(thickness = 2.dp)
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    LazyColumn(state = listState,
                                        modifier = Modifier.autoComplete(autoCompleteState),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        autoCompleteState.filteredItems.sortedBy { !it.isFavorite.value }.forEach {
                                            item {
                                                ScriptSurface(modifier = Modifier.fillMaxWidth()
                                                    .clickable { onItemSelected(it.value) }
                                                ) {
                                                    suggestedItemComponent(it)
                                                }
                                            }
                                        }
                                    }
                                    VerticalScrollbar(
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                            .heightIn(0.dp, autoCompleteState.maxHeight),
                                        adapter = rememberScrollbarAdapter(scrollState = listState),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        autoCompleteState.content()
    }
}




