package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.section.BookshelfSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.PrimaryActionButton
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_btn_add
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_bookshelf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.BookshelfScreen(
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    AdaptiveNavigationSuiteScaffold(
        modifier = modifier,
        primaryActionContent = {
            PrimaryActionButton(
                text = { Text(text = stringResource(Res.string.bookshelf_btn_add)) },
                icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
                onClick = onFabClick,
            )
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(Res.string.bookshelf_label_bookshelf)) },
                    actions = { SettingsIconButton(onClick = onSettingsClick) },
                )
            },
        ) {
            BookshelfSheet(
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                onBookshelfClick = onBookshelfClick,
                onBookshelfInfoClick = onBookshelfInfoClick,
                contentPadding = it,
            )
        }
    }
}
