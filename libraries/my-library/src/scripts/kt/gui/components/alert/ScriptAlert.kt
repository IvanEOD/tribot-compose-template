package scripts.kt.gui.components.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import scripts.kt.gui.ScriptText
import scripts.kt.gui.components.toMutableState
import scripts.kt.gui.theme.ScriptColorProvider


/* Written by IvanEOD 9/26/2022, at 2:16 PM */
@Stable
@Immutable
data class ScriptGuiAlertState(
    val showing: MutableState<Boolean> = false.toMutableState(),
    val title: MutableState<String> = "".toMutableState(),
    val message: MutableState<String> = "".toMutableState(),
    val confirmButtonText: MutableState<String> = "Confirm".toMutableState(),
    val cancelButtonText: MutableState<String> = "Cancel".toMutableState(),
    val result: MutableState<Boolean> = false.toMutableState(),
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScriptGuiAlert(
    state: ScriptGuiAlertState,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    val (result, setResult) = remember { state.result }
    val confirmColor = ScriptColorProvider.colors.primary
    val cancelColor = ScriptColorProvider.colors.error

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Black),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            AlertDialog(
                modifier = Modifier.defaultMinSize(minWidth = 300.dp, minHeight = 200.dp),
                onDismissRequest = {
                    setResult(false)
                    state.showing.value = false
                    onCancel()
               },
                title = { ScriptText(text = state.title.value) },
                text = { ScriptText(text = state.message.value) },
                confirmButton = {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = confirmColor,
                            backgroundColor = confirmColor,
                        ),
                        onClick = {
                            state.result.value = true
                            setResult(true)
                            state.showing.value = false
                            onConfirm()
                        }
                    ) {
                        ScriptText(text = state.confirmButtonText.value)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = cancelColor,
                            contentColor = cancelColor,
                        ),
                        onClick = {
                            state.result.value = false
                            setResult(false)
                            state.showing.value = false
                            onCancel()
                        }
                    ) {
                        ScriptText(text = state.cancelButtonText.value)
                    }
                }
            )

        }

    }


}