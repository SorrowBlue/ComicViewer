package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterGraphTransitions
import com.sorrowblue.comicviewer.folder.FolderArgs
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Destination<ReadLaterGraph>(
    navArgs = FolderArgs::class,
    style = ReadLaterGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun ReadLaterFolderScreen(
    args: FolderArgs,
    navigator: FolderScreenNavigator,
) {
    FolderScreen(args = args, navigator = navigator)
}
