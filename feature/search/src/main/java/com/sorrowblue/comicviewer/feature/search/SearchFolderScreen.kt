package com.sorrowblue.comicviewer.feature.search

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraphTransitions
import com.sorrowblue.comicviewer.folder.FolderArgs
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Destination<SearchGraph>(
    navArgs = FolderArgs::class,
    style = SearchGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun SearchFolderScreen(
    args: FolderArgs,
    navigator: FolderScreenNavigator,
) {
    FolderScreen(args = args, navigator = navigator)
}
