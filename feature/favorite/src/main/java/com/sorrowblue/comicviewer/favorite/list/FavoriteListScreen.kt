package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphTransitions
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteListEmptySheet
import com.sorrowblue.comicviewer.favorite.section.FavoriteListSheet
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

interface FavoriteListNavigator {
    fun onSettingsClick()
    fun onFavoriteClick(favoriteId: FavoriteId)
    fun onNewFavoriteClick()
}

@Destination<FavoriteGraph>(
    start = true,
    style = FavoriteGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FavoriteListScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: FavoriteListNavigator,
) {
    FavoriteListScreen(
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onSettingsClick = navigator::onSettingsClick,
        onNewFavoriteClick = navigator::onNewFavoriteClick,
        onFavoriteClick = navigator::onFavoriteClick
    )
}

@Composable
private fun FavoriteListScreen(
    savedStateHandle: SavedStateHandle,
    onSettingsClick: () -> Unit,
    onNewFavoriteClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    state: FavoriteListScreenState = rememberFavoriteListScreenState(savedStateHandle = savedStateHandle),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteListScreen(
        lazyPagingItems = lazyPagingItems,
        lazyListState = state.lazyListState,
        onSettingsClick = onSettingsClick,
        onFavoriteClick = onFavoriteClick,
        onCreateFavoriteClick = onNewFavoriteClick
    )
    NavTabHandler(onClick = state::onNavClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteListScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    lazyListState: LazyListState,
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    onCreateFavoriteClick: () -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            FavoriteListAppBar(
                scrollBehavior = appBarScrollBehavior,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(R.string.favorite_btn_create)) },
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = "") },
                onClick = onCreateFavoriteClick
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            FavoriteListEmptySheet(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            FavoriteListSheet(
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding.add(paddingValues = PaddingValues(bottom = 88.dp)),
                lazyListState = lazyListState,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}
