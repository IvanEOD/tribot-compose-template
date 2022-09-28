# tribot-compose-template
 Example Jetpack Compose Tribot Script GUI


# Features

- Directory Interface for easily saving and loading data.
```kt
// Saving
Directory.Script.save(myClass, "MySaveName")
// Loading
val data = Directory.Script.load(myClass::class.java, "MyFolder", "MyLoadName")
```

- GitHub class for easily accessing files from GitHub.
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
