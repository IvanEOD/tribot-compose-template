package scripts

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Build
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.tribot.script.sdk.Log
import scripts.kt.gui.ScriptIcon
import scripts.kt.gui.ScriptText
import scripts.kt.gui.buildGui
import scripts.kt.gui.components.ScriptIcon
import scripts.kt.gui.windowFrameIcons


/* Written by IvanEOD 9/28/2022, at 11:05 AM */

val myScriptGui = buildGui("My Script", ScriptIcon.fromGithubImage("${windowFrameIcons.random()}%20icon")) {

    Screen("Page One", ScriptIcon.fromGithubImage("one")) {

        onGuiClosed {
            Log.trace("Gui Closed (Page One)")
        }

        FloatingAction {

            ExtendedFloatingActionButton(
                text = { ScriptText("Click Me!") },
                onClick = { Log.trace("Clicked Floating Action Button!") },
                icon = { ScriptIcon(Icons.TwoTone.Build) }
            )

        }

        MainFrame {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(onClick = {
                    toggleLeftFrame()
                }) {
                    ScriptText("Toggle Left Frame")
                }
                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    toggleRightFrame()
                }) {
                    ScriptText("Toggle Right Frame")
                }

            }

        }

        LeftFrame {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(onClick = {
                    toggleLeftFrame()
                }) {
                    ScriptText("Close Left Frame")
                }

            }

        }

        RightFrame {

            Column(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(onClick = {
                    toggleRightFrame()
                }) {
                    ScriptText("Close Right Frame")
                }

                Button(onClick = {
                    guiScope.navigation.navigate("Page One Settings")
                }) {
                    ScriptText("Go to Page One Settings")
                }

            }

        }

        Screen("Settings", "Page One Settings") {

            MainFrame {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    ScriptText("Page One Settings")

                }
            }

        }

    }

    Screen("Page Two", ScriptIcon.fromGithubImage("two")) {

        LeftFrame {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                ScriptText("Left Frame Stays Open")
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        guiScope.scope.launch {
                            guiScope.snackbarResult = guiScope.dispatchSnackbar(
                                "This is a snackbar!",
                                "Click Me!",
                                SnackbarDuration.Long
                            )

                            Log.info("Snackbar Result: ${guiScope.snackbarResult}")
                        }
                    }
                ) {
                    ScriptText("Show Snackbar")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        guiScope.scope.launch {
                            guiScope.dispatchAlert(
                                "Script Alert!",
                                "You are testing the alert!",
                                "Ok",
                                "Close",
                                { Log.trace("Alert Confirmed") },
                                { Log.trace("Alert Cancelled") }
                            )
                        }


                    }
                ) {
                    ScriptText("Show Alert")
                }
            }
        }


        MainFrame {
            if (!isLeftFrameVisible()) showLeftFrame()
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ScriptText("Page Two")
            }

        }

    }


}
