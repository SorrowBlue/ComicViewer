package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.folder.navigation.FolderArgs

interface BookshelfFolderScreenNavigator : FolderScreenNavigator {
    fun onRestoreComplete()
}

@Destination(navArgsDelegate = FolderArgs::class)
@Composable
internal fun BookshelfFolderScreen(
    args: FolderArgs,
    navBackStackEntry: NavBackStackEntry,
    navigator: BookshelfFolderScreenNavigator,
) {
    FolderScreen(
        args = args,
        savedStateHandle = navBackStackEntry.savedStateHandle,
        navigator = navigator,
        onRestoreComplete = navigator::onRestoreComplete
    )
}
