package com.sorrowblue.comicviewer.app.component

import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

sealed class MainScreenTab(
    val navGraph: Any,
    open val label: String,
    val icon: ImageVector,
) {

    data class Bookshelf(override val label: String) :
        MainScreenTab("TODO()", label, ComicIcons.Book)

    data class Favorite(override val label: String) :
        MainScreenTab("TODO()", label, ComicIcons.Favorite)

    data class Readlater(override val label: String) :
        MainScreenTab("TODO()", label, ComicIcons.WatchLater)

    data class Library(override val label: String) :
        MainScreenTab("TODO()", label, ComicIcons.LibraryBooks)

    companion object {
        val entries: List<MainScreenTab>
            get() = listOf(
                Bookshelf(""),
                Favorite(""),
                Readlater(""),
                Library("")
            )
    }
}
