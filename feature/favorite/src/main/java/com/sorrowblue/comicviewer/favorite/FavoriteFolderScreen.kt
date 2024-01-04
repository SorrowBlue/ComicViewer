package com.sorrowblue.comicviewer.favorite

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.folder.navigation.FolderArgs

@Destination(navArgsDelegate = FolderArgs::class)
@Composable
internal fun FavoriteFolderScreen(
    args: FolderArgs,
    navBackStackEntry: NavBackStackEntry,
    navigator: FolderScreenNavigator,
) {
    FolderScreen(
        args = args,
        savedStateHandle = navBackStackEntry.savedStateHandle,
        navigator = navigator
    )
}
