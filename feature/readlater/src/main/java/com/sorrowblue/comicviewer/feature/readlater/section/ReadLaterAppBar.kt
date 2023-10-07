package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.readlater.R
import com.sorrowblue.comicviewer.file.component.FileContentLayout
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.AppBarAction
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import kotlinx.collections.immutable.PersistentList

enum class ReadLaterAction(override val icon: ImageVector, override val label: String) :
    AppBarAction {

    FileContetView(ComicIcons.ViewList, "list"),
    FileContetGrid(ComicIcons.GridView, "grid"),
    GridSize(ComicIcons.Grid4x4, "Change Grid size"),
    Clear(ComicIcons.ClearAll, "Clear Read Later"),
    Settings(ComicIcons.Settings, "Settings"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReadLaterAppBar(
    list: PersistentList<ReadLaterAction>,
    fileContentLayout: FileContentLayout,
    topAppBarScrollBehavior: TopAppBarScrollBehavior? = null,
    onFileContentLayoutClick: () -> Unit = {},
    onGridSizeClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(stringResource(R.string.readlater_title)) },
        actions = list,
        onClick = {
            when (it) {
                ReadLaterAction.FileContetView -> onFileContentLayoutClick()
                ReadLaterAction.FileContetGrid -> onFileContentLayoutClick()
                ReadLaterAction.GridSize -> onGridSizeClick()
                ReadLaterAction.Clear -> onClearAllClick()
                ReadLaterAction.Settings -> onSettingsClick()
            }
        }
    )
//    TopAppBar(
//        title = { Text(stringResource(R.string.readlater_title)) },
//        actions = {
//            FileContentLayoutButton(
//                fileContentLayout = fileContentLayout,
//                onClick = onFileContentLayoutClick
//            )
//            val overflowMenuState = rememberOverflowMenuState()
//            OverflowMenu(overflowMenuState) {
//                if (fileContentLayout is FileContentLayout.Grid) {
//                    DropdownMenuItem(
//                        text = { Text(text = "Change Grid size") },
//                        trailingIcon = { Icon(ComicIcons.Grid4x4, "Change grid size") },
//                        onClick = {
//                            overflowMenuState.collapse()
//                            onGridSizeClick()
//                        }
//                    )
//                }
//                DropdownMenuItem(
//                    text = { Text("Clear Read Later") },
//                    trailingIcon = {
//                        Icon(
//                            ComicIcons.ClearAll,
//                            "Clear Read Later"
//                        )
//                    },
//                    onClick = {
//                        overflowMenuState.collapse()
//                        onClearAllClick()
//                    }
//                )
//                DropdownMenuItem(
//                    text = { Text(text = "Settings") },
//                    trailingIcon = {
//                        Icon(
//                            ComicIcons.Settings,
//                            "Settings"
//                        )
//                    },
//                    onClick = {
//                        overflowMenuState.collapse()
//                        onSettingsClick()
//                    }
//                )
//            }
//        },
//        scrollBehavior = topAppBarScrollBehavior
//    )
}
