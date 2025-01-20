package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteNameField
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarBottom
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.itemKey
import comicviewer.feature.favorite.edit.generated.resources.Res
import comicviewer.feature.favorite.edit.generated.resources.favorite_edit_text_no_favorites
import comicviewer.feature.favorite.edit.generated.resources.favorite_edit_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import soil.form.FormPolicy
import soil.form.compose.Controller
import soil.form.compose.Form
import soil.form.compose.rememberSubmissionRuleAutoControl

interface FavoriteEditScreenNavigator {
    fun navigateUp()
    fun onComplete()
}

@Serializable
data class FavoriteEdit(val favoriteId: FavoriteId)

@Destination<FavoriteEdit>
@Composable
internal fun FavoriteEditScreen(
    route: FavoriteEdit,
    navigator: FavoriteEditScreenNavigator,
    state: FavoriteEditScreenState = rememberFavoriteEditScreenState(route = route),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteEditScreen(
        uiState = state.uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = navigator::navigateUp,
        onSaveClick = { state.onSaveClick(it) },
        onDeleteClick = state::onDeleteClick,
    )

    EventEffect(state.events) {
        when (it) {
            FavoriteEditScreenStateEvent.EditComplete -> navigator.onComplete()
        }
    }
}

data class FavoriteEditScreenUiState(
    val initName: String = "",
    val error: String = "",
)

@Composable
internal fun FavoriteEditScreen(
    uiState: FavoriteEditScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onSaveClick: (String) -> Unit,
    onDeleteClick: (File) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
) {
    if (uiState.initName.isEmpty()) return
    Form(
        onSubmit = { onSaveClick(it) },
        initialValue = uiState.initName,
        policy = FormPolicy.Default
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        modifier = Modifier.wrapContentHeight(),
                        title = { Text(stringResource(Res.string.favorite_edit_title)) },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(ComicIcons.ArrowBack, null)
                            }
                        },
                        actions = {
                            Controller(
                                rememberSubmissionRuleAutoControl()
                            ) {
                                IconButton(onClick = it.onSubmit, enabled = it.canSubmit) {
                                    Icon(ComicIcons.Save, null)
                                }
                            }
                        },
                        windowInsets = WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                        ),
                        scrollBehavior = scrollBehavior
                    )
                    TopAppBarBottom(scrollBehavior = scrollBehavior) {
                        FavoriteNameField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ComicTheme.dimension.margin),
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { contentPadding ->
            if (lazyPagingItems.isEmptyData) {
                EmptyContent(
                    imageVector = ComicIcons.UndrawNoData,
                    text = stringResource(Res.string.favorite_edit_text_no_favorites),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )
            } else {
                LazyColumn(
                    state = lazyListState,
                    contentPadding = contentPadding,
                    modifier = Modifier
                    // TODO .drawVerticalScrollbar(lazyListState)
                ) {
                    items(
                        lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { "${it.bookshelfId.value}${it.path}" }
                    ) {
                        val item = lazyPagingItems[it]
                        if (item != null) {
                            ListItem(
                                headlineContent = { Text(item.name) },
                                leadingContent = {
                                    AsyncImage(
                                        model = FileThumbnail.from(item),
                                        null,
                                        Modifier.size(56.dp)
                                    )
                                },
                                trailingContent = {
                                    IconButton(onClick = { onDeleteClick(item) }) {
                                        Icon(ComicIcons.Delete, null)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
