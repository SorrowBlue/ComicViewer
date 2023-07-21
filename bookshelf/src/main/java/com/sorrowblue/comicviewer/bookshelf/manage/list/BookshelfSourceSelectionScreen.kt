package com.sorrowblue.comicviewer.bookshelf.manage.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.ui.AppBarConfiguration
import com.sorrowblue.comicviewer.bookshelf.manage.BookshelfSource
import com.sorrowblue.comicviewer.framework.compose.AppScaffold
import com.sorrowblue.comicviewer.framework.compose.copy

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookshelfSourceSelectionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    appBarConfiguration: AppBarConfiguration
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val state = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(state)
    val nestedScrollConnection = rememberNestedScrollInteropConnection()
    AppScaffold(
        navController = navController,
        navBackStackEntry = navBackStackEntry,
        scrollBehavior = scrollBehavior,
        appBarConfiguration = appBarConfiguration,
        modifier = modifier
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding.copy(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {
            items(BookshelfSource.values()) {
                BookshelfSourceRow(source = it, modifier = Modifier.clickable {
                    when (it) {
                        BookshelfSource.DEVICE -> navController.navigate(
                            BookshelfManageListFragmentDirections.actionBookshelfManageListToBookshelfManageDevice()
                        )

                        BookshelfSource.SMB -> navController.navigate(
                            BookshelfManageListFragmentDirections.actionBookshelfManageListToBookshelfManageSmb()
                        )
                    }
                })
            }
        }
    }
}
