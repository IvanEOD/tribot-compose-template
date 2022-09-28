# tribot-compose-template
 Example Jetpack Compose Tribot Script GUI
 
# Script Gui Builder

- [ScriptGuiBuilder.kt](libraries/my-library/src/scripts/kt/gui/ScriptGuiBuilder.kt)
- [MyScriptGui.kt](scripts/my-script/src/scripts/MyScriptGui.kt)
- [BaseGui.kt](libraries/my-library/src/scripts/kt/gui/components/BaseGui.kt)

  - Anywhere inside the [GuiScope]() you have access to: 
  
    - ```navigation``` [NavigationController](libraries/my-library/src/scripts/kt/gui/components/NavigationController.kt)
    - ```currentScreen``` [GuiScreen](libraries/my-library/src/scripts/kt/gui/components/GuiScreen.kt)
    - ```dispatchSnackbar(message, action, duration, onAction)```
    - ```dispatchAlert(title, message, confirmButtonText, cancelButtonText, onConfirm, onCancel)``` [ScriptGuiAlert](libraries/my-library/src/scripts/kt/gui/components/alert/ScriptAlert.kt)
    - ```navigateTo(navigationKey)```
    - ```navigateTo([GuiScreen](libraries/my-library/src/scripts/kt/gui/components/GuiScreen.kt))```
    - ```toggleCurrentScreenLeftFrame()```
    - ```toggleCurrentScreenRightFrame()```
    - ```closeGui()```

  - Gui
    
    ```kt
    buildGui("Title", ScriptIcon.fromGithubImage("${windowFrameIcons.random()}%20icon")) {
       // GuiScope inside here
    }
    ```

  - Screen
  
    ```kt
    Screen("Title", ScriptIcon.fromGithubImage("name")) {

        onGuiClosed {
           
        }

        FloatingAction {

        }

        MainFrame {

        }

        LeftFrame {

        }

        RightFrame {

        }

        Screen("Title", "unique name") {
           
        }

    }
    ```




# Included Features

<details><summary>Directory :file_folder:</summary>
<p>

## [Directory](/libraries/my-library/src/scripts/kt/utility/Directory.kt) Interface for easily saving and loading data. 

- The following references to ```SCRIPT_DIRECTORY``` is set in the [ScriptData](libraries/my-library/src/scripts/kt/ScriptData.kt)

- Directory.TRiBot
  - ```.../.tribot/```
- Directory.Root
  - ```.../.tribot/SCRIPTER_DIRECTORY/```
- Directory.Script
  - ```.../.tribot/SCRIPTER_DIRECTORY/script_name/```
- Directory.Account
  - ```.../.tribot/SCRIPTER_DIRECTORY/account_name/```
- Directory.Settings
  - ```.../.tribot/settings/```

- Saving
```kt
Directory.Script.save(myClass, "MySaveName")
// Saves the file in .../.tribot/SCRIPTER_DIRECTORY/script_name/MySaveName.json
```
- Loading
```kt
val data = Directory.Script.load(myClass::class.java, "MyFolder", "MyLoadName") 
// Loads the file from .../.tribot/SCRIPTER_DIRECTORY/script_name/MyFolder/MyLoadName.json
```
- Cleaning Directories
```kt
Directory.Script.clean() 
// Deletes all files older than 5 days in the Script directory

Directory.Script.clean(1, TimeUnit.DAYS, true) 
// Deletes all files older than 1 day in the Script directory

```
</p>
</details>


<details><summary>GitHub :octocat:</summary>
<p>

- GitHub class for easily accessing files from GitHub.
  - You can set your repository and auth key in [ScriptData](/libraries/my-library/src/scripts/kt/ScriptData.kt)
  - GitHub files are saved under ```.../.tribot/SCRIPTER_DIRECTORY/github/directory```
  - Your GitHub has to be structured accordingly. Your root Url should lead to the directory containing these directories:
    - css
    - fonts
    - fxml
    - images
    - json

```kt
val jsonFile = GitHub.getJson("jsonName")
val fontFile = GitHub.getFont("fontName")
val imageFile = GitHub.getImageFile("imageName")
val javafxImage = GitHub.getImage("imageName")
val cssFile = GitHub.getCss("cssName")
val fxmlFile = GitHub.getFxml("fxmlName")
val file = GitHub.getFile("fileName.txt")
val fileInDirectory = GitHub.getFile("directoryName", "fileName.txt")
```

</p>
</details>

<details><summary>Asset Manager :package:</summary>
<p>

- AssetManager for loading and caching data to help save resources

```kt
val gitHubImageBitmap = AssetManager.loadGithubImageToBitmap("imageName")
val gitHubImagePainter = AssetManager.loadGithubImageToPainter("imageName")
val urlImageBitmap = AssetManager.loadUrlImageToBitmap("url")
val urlImagePainter = AssetManager.loadUrlImageToPainter("url")
val wikiImageBitmap = AssetManager.loadWikiImageToBitmap("imageName")
val wikiImagePainter = AssetManager.loadWikiImageToPainter("imageName")
// These are set up slightly different to access the item icons folder, it will only get items currently in the folder. Right now it's the latest OsrsBox list.
val itemIconByIDBitmap = AssetManager.loadGithubItemIconToBitmap(995)
val itemIconByIDPainter = AssetManager.loadGithubItemIconToPainter(995)
```

</p>
</details>
