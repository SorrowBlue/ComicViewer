package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfGraph
import com.sorrowblue.comicviewer.feature.folder.destinations.SortTypeDialogDestination
import com.sorrowblue.comicviewer.folder.FolderArgs
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Destination<BookshelfGraph>(navArgs = FolderArgs::class)
@Composable
internal fun BookshelfFolderScreen(
    args: FolderArgs,
    navigator: FolderScreenNavigator,
    sortTypeResultRecipient: ResultRecipient<SortTypeDialogDestination, SortType>,
) {
    FolderScreen(
        args = args,
        navigator = navigator,
        sortTypeResultRecipient = sortTypeResultRecipient
    )
}
