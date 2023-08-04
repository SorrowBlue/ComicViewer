package com.sorrowblue.comicviewer.folder.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.sorrowblue.comicviewer.framework.resource.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderTopAppBar(
    title: String,
    onClickSearch: () -> Unit,
    onClickSortDisplay: () -> Unit,
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(title) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = navController::popBackStack) {
                Icon(Icons.TwoTone.ArrowBack, "")
            }
        },
        actions = {
            PlainTooltipBox(tooltip = { Text("検索") }) {
                IconButton(onClick = onClickSearch, modifier = Modifier.tooltipAnchor()) {
                    Icon(Icons.TwoTone.Search, "検索")
                }
            }
            PlainTooltipBox(tooltip = { Text("ソート／表示設定") }) {
                IconButton(
                    onClick = onClickSortDisplay,
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(Icons.TwoTone.ViewList, "ソート／表示設定")
                }
            }
            PlainTooltipBox(tooltip = { Text(stringResource(id = R.string.framework_label_open_settings)) }) {
                IconButton(
                    onClick = { navController.navigate(com.sorrowblue.comicviewer.folder.R.id.action_global_settings_navigation) },
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(
                        Icons.TwoTone.Settings,
                        stringResource(id = R.string.framework_label_open_settings)
                    )
                }
            }
        }
    )
}
