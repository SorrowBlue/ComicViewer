package com.sorrowblue.comicviewer.bookshelf

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.folder.FolderArgs
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

interface BookshelfFolderScreenNavigator : FolderScreenNavigator {
    fun onRestoreComplete()
}

@Destination(navArgsDelegate = FolderArgs::class)
@Composable
internal fun BookshelfFolderScreen(
    args: FolderArgs,
    navigator: BookshelfFolderScreenNavigator,
) {
    FolderScreen(
        args = args,
        navigator = navigator,
        onRestoreComplete = navigator::onRestoreComplete
    )
}
