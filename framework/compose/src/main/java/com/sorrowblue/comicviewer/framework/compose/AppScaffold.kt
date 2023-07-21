package com.sorrowblue.comicviewer.framework.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppScaffold(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry?,
    scrollBehavior: TopAppBarScrollBehavior,
    appBarConfiguration: AppBarConfiguration,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) = Scaffold(
    modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
        AppCenterAlignedTopAppBar(
            title = {
                Text(
                    navBackStackEntry?.destination?.label?.toString().orEmpty(),
                    modifier = Modifier.basicMarquee()
                )
            },
            navController = navController,
            navBackStackEntry = navBackStackEntry,
            appBarConfiguration = appBarConfiguration,
            scrollBehavior = scrollBehavior
        )
    },
    bottomBar,
    snackbarHost,
    floatingActionButton,
    floatingActionButtonPosition,
    containerColor,
    contentColor,
    contentWindowInsets,
    content
)
