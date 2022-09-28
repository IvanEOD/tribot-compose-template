package scripts.kt.gui.components.autocomplete

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import scripts.kt.utility.searching.FuzzySearch


/* Written by IvanEOD 9/22/2022, at 12:46 PM */

fun Modifier.autoComplete(scope: AutoCompleteDesignScope): Modifier = composed {
    val baseModifier = if (scope.wrapContentHeight) wrapContentHeight() else heightIn(0.dp, scope.maxHeight)
    baseModifier.testTag("AutoCompleteBox")
}

fun <T> List<T>.asAutoCompleteItems(
    itemToString: (T) -> String = { it.toString() },
    isFavorite: (T) -> Boolean = { false },
): List<AutoCompleteSupplier<T>> = map {
    object : AutoCompleteSupplier<T> {
        override val value = it
        override val asString = { itemToString(value) }
        override var isFavorite = mutableStateOf(isFavorite(it))
    }
}

@Stable
interface AutoCompleteSupplier<T> {
    val value: T
    val asString: () -> String
    val isFavorite: MutableState<Boolean>
}

@Stable
interface AutoCompleteScope<T> : AutoCompleteDesignScope {
    var isSearching: Boolean
    var listSelectionIndex: Int
    fun filter(query: String)
    fun onItemSelected(block: (T) -> Unit = {})
}

@Stable
interface AutoCompleteDesignScope {
    var width: Float
    var wrapContentHeight: Boolean
    var maxHeight: Dp
    var border: BorderStroke
    var shape: Shape
}

class AutoCompleteState<T>(
    private val listBuilder: ((String) -> List<T>)? = null,
    private val favoritesListBuilder: ((String) -> List<T>)? = null,
    internal val startItems: List<T> = if (listBuilder != null) listBuilder("") else emptyList(),
    private val favoritesList: MutableState<List<T>> = if (favoritesListBuilder != null) mutableStateOf(
        favoritesListBuilder("")) else mutableStateOf(emptyList()),
    val itemToString: (T) -> String = { it.toString() },
) : AutoCompleteScope<T> {

    override var isSearching by mutableStateOf(false)
    override var listSelectionIndex by mutableStateOf(-1)
    override var width by mutableStateOf(0.9f)
    override var wrapContentHeight by mutableStateOf(false)
    override var maxHeight: Dp by mutableStateOf(TextFieldDefaults.MinHeight * 3)
    override var border by mutableStateOf(BorderStroke(2.dp, Color.Black))
    override var shape: Shape by mutableStateOf(RoundedCornerShape(8.dp))

    private val onItemSelectedActions: MutableSet<(T) -> Unit> = mutableSetOf()
    private var autoCompleteItems by mutableStateOf(emptyList<AutoCompleteSupplier<T>>())
    var filteredItems by mutableStateOf(emptyList<AutoCompleteSupplier<T>>())
    val textFieldValue: MutableState<String> = mutableStateOf("")

    private fun isFavorite(item: T): Boolean = favoritesList.value.contains(item)


    fun selectItem(item: T?) {
        if (item != null) {
            onItemSelectedActions.forEach { it(item) }
            textFieldValue.value = itemToString(item)
        } else textFieldValue.value = ""
        filteredItems = emptyList()
    }

    override fun filter(query: String) {
        if (listBuilder == null) {
            if (autoCompleteItems.isEmpty()) {
                autoCompleteItems = startItems.asAutoCompleteItems(itemToString, isFavorite = ::isFavorite)
            }
        } else {
            autoCompleteItems =
                listBuilder.let { it(query) }.asAutoCompleteItems(itemToString, isFavorite = ::isFavorite)
        }
        filteredItems = fuzzySearch(query, autoCompleteItems)
    }

    private fun fuzzySearch(query: String, list: List<AutoCompleteSupplier<T>>): List<AutoCompleteSupplier<T>> {
        val results = FuzzySearch.extractSorted(query, list, { it.asString() }, 50)
        return results.map { it.referent }
    }

    override fun onItemSelected(block: (T) -> Unit) {
        onItemSelectedActions.add(block)
    }

}