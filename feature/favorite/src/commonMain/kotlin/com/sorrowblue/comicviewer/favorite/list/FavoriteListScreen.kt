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
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteListContents
import com.sorrowblue.comicviewer.favorite.section.FavoriteListContentsAction
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalContainerColor
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import comicviewer.feature.favorite.generated.resources.Res
import comicviewer.feature.favorite.generated.resources.favorite_btn_create
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

interface FavoriteListNavigator {
    fun onSettingsClick()
    fun onNewFavoriteClick()
    fun navigateToEdit(favoriteId: FavoriteId)
    fun navigateToFavorite(favoriteId: FavoriteId)
}

@Serializable
data object FavoriteList

@Destination<FavoriteList>
@Composable
internal fun FavoriteListScreen(
    navigator: FavoriteListNavigator = koinInject(),
    state: FavoriteListScreenState = rememberFavoriteListScreenState(),
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
                is FavoriteListContentsAction.DeleteClick -> {
                    state.delete(it.favoriteId)
                }
            }
        }
    )
    // TODO NavTabHandler(onClick = state::onNavClick)
}

@Composable
internal fun FavoriteListScreen(
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
                onSettingsClick = onSettingsClick,
                scrollBehavior = appBarScrollBehavior,
                scrollableState = lazyListState
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(Res.string.favorite_btn_create)) },
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
                onClick = onCreateFavoriteClick
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = LocalContainerColor.current,
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
