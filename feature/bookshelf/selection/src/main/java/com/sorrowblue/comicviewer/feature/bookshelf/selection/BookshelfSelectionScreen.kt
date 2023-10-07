package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.component.BookshelfSourceRow
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.responsive.FullScreenTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfSelectionScreen(
    onBackClick: () -> Unit,
    onSourceClick: (BookshelfType) -> Unit,
) {
    val windowSizeClass: WindowSizeClass = LocalWindowSize.current
    val isCompact = remember(windowSizeClass.widthSizeClass) {
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            FullScreenTopAppBar(
                title = { Text(stringResource(R.string.bookshelf_selection_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = if (isCompact) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->

        val padding = if (isCompact) {
            paddingValues
                .add(
                    PaddingValues(
                        start = ComicTheme.dimension.margin,
                        end = ComicTheme.dimension.margin,
                        bottom = ComicTheme.dimension.margin
                    )
                )
        } else {
            paddingValues
                .add(
                    paddingValues = PaddingValues(
                        start = ComicTheme.dimension.margin,
                        end = ComicTheme.dimension.margin,
                        top = ComicTheme.dimension.spacer,
                        bottom = ComicTheme.dimension.margin,
                    )
                )
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding * 2),
            horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding * 2)
        ) {
            items(BookshelfType.values() + BookshelfType.values() + BookshelfType.values() + BookshelfType.values() + BookshelfType.values()) { type ->
                BookshelfSourceRow(
                    type = type,
                    onClick = { onSourceClick(type) }
                )
            }
        }
    }
}
