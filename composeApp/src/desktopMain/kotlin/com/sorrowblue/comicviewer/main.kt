package com.sorrowblue.comicviewer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sorrowblue.comicviewer.app.ComicViewerApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComicViewer",
    ) {
        RootScreenWrapper(finishApp = ::exitApplication) {
            ComicViewerApp()
        }
    }
}
