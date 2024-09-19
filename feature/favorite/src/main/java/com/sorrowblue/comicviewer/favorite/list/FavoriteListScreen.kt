package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphTransitions
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteListContents
import com.sorrowblue.comicviewer.favorite.section.FavoriteListContentsAction
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler

interface FavoriteListNavigator {
    fun onSettingsClick()
    fun onNewFavoriteClick()
    fun navigateToEdit(favoriteId: FavoriteId)
    fun navigateToFavorite(favoriteId: FavoriteId)
}

@Destination<FavoriteGraph>(
    start = true,
    style = FavoriteGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FavoriteListScreen(navigator: FavoriteListNavigator) {
    FavoriteListScreen(
        navigator = navigator,
        state = rememberFavoriteListScreenState(),
    )
}

@Composable
private fun FavoriteListScreen(
    navigator: FavoriteListNavigator,
    state: FavoriteListScreenState,
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteListScreen(
        lazyPagingItems = lazyPagingItems,
        lazyListState = state.lazyListState,
        onSettingsClick = navigator::onSettingsClick,
        onCreateFavoriteClick = navigator::onNewFavoriteClick,
        onContentsAction = {
            when (it) {
                is FavoriteListContentsAction.EditClick -> navigator.navigateToEdit(it.favoriteId)
                is FavoriteListContentsAction.FavoriteClick -> navigator.navigateToFavorite(it.favoriteId)
            }
        }
    )
    NavTabHandler(onClick = state::onNavClick)
}

@Composable
private fun FavoriteListScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    lazyListState: LazyListState,
    onSettingsClick: () -> Unit,
    onCreateFavoriteClick: () -> Unit,
    onContentsAction: (FavoriteListContentsAction) -> Unit,
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
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
                onClick = onCreateFavoriteClick
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        FavoriteListContents(
            lazyPagingItems = lazyPagingItems,
            onAction = onContentsAction,
            lazyListState = lazyListState,
            contentPadding = innerPadding,
        )
    }
}
