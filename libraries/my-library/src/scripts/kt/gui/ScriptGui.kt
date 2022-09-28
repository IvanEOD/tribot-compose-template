package scripts.kt.gui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import scripts.kt.gui.components.*


/* Written by IvanEOD 9/28/2022, at 9:27 AM */

@Stable
@Immutable
class ScriptGui(
    val title: String,
    val icon: ScriptIcon,
    screenList: List<GuiScreen>,
    val onGuiClosed: () -> Unit = { },
) {
    private val screensWithHomeAndSettings = listOf(HomeScreen) + screenList + SettingsScreen
    val screens = screensWithHomeAndSettings.toMutableStateList()
    var state: GuiState by mutableStateOf(GuiState.Running)
}

val HomeScreen = buildScreen("Home", ScriptIcon.fromImageVector(Icons.TwoTone.Home)) {



}



val SettingsScreen = buildScreen("Settings", ScriptIcon.fromImageVector(Icons.TwoTone.Settings)) {

    MainFrame {
        Column(
            modifier = Modifier.fillMaxSize().padding(all = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            val antibanOpened = remember { mutableStateOf(false) }
            val otherOpened = remember { mutableStateOf(false) }

            val textInput1 = remember { mutableStateOf("") }
            val textInput2 = remember { mutableStateOf("") }


            ToggleableTitledSection("Antiban", enabledState = antibanOpened) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    LabeledTextInput(
                        "Text Input Settings",
                        labelWidth = 200.dp,
                        inputWidth = 200.dp,
                        textValue = textInput1)
                }
            }
            ToggleableTitledSection("Other", enabledState = otherOpened) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    LabeledTextInput(
                        "Text Input Settings",
                        labelWidth = 200.dp,
                        inputWidth = 200.dp,

                        textValue = textInput2,
                        trailingIcon = {
                            ScriptIcon(
                                Icons.TwoTone.Close,
                                modifier = Modifier.clickable {
                                    textInput2.value = ""
                                }
                            )
                        }
                    )
                }
            }

        }


    }

}
