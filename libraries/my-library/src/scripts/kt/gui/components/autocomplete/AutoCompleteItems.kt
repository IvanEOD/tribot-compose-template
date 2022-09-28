package scripts.kt.gui.components.autocomplete

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import scripts.kt.gui.FavoriteStarToggleButton
import scripts.kt.gui.ScriptText


/* Written by IvanEOD 9/22/2022, at 12:55 PM */

@Composable
fun AutoCompleteItemWithFavorites(item: String, isFavorite: MutableState<Boolean>) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp, 2.dp, 16.dp, 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(TextFieldDefaults.MinHeight / 2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScriptText(text = item, style = MaterialTheme.typography.caption)
            FavoriteStarToggleButton(isFavorite)
        }
    }
}

@Composable
fun AutoCompleteItem(item: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp, 2.dp, 16.dp, 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(TextFieldDefaults.MinHeight / 2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScriptText(text = item, style = MaterialTheme.typography.caption)
        }
    }
}