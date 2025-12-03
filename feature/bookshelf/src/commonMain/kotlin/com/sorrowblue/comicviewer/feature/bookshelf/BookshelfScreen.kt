package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfAppBar
import com.sorrowblue.comicviewer.feature.bookshelf.section.BookshelfSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.PrimaryActionButton
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_btn_add
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
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            topBar = {
                BookshelfAppBar(
                    onSettingsClick = onSettingsClick,
                    scrollBehavior = scrollBehavior,
                )
            },
            containerColor = ComicTheme.colorScheme.surface,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { contentPadding ->
            val additionalPaddings = if (navigationSuiteType.isNavigationRail) {
                PaddingValues(ComicTheme.dimension.margin)
            } else {
                PaddingValues(
                    start = ComicTheme.dimension.margin,
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin,
                )
            }
            BookshelfSheet(
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                onBookshelfClick = onBookshelfClick,
                onBookshelfInfoClick = onBookshelfInfoClick,
                contentPadding = contentPadding + additionalPaddings,
            )
        }
    }
}
