package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_label_next_book
import comicviewer.feature.book.generated.resources.book_label_page_count
import comicviewer.feature.book.generated.resources.book_label_prev_book
import kotlin.math.max
import org.jetbrains.compose.resources.stringResource

/**
 * Book bottom bar
 *
 * @param pageRange 1から最大ページ数の間
 * @param currentPage 現在のページ数 ０から最大ページ数+1
 * @param onPageChange 現在のページが変更された場合
 * @receiver
 */
@Composable
internal fun BookBottomBar(
    pageRange: ClosedFloatingPointRange<Float>,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalFloatingToolbar(
            expanded = false,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        ) {
            Text(
                text = when {
                    currentPage < 1 -> stringResource(Res.string.book_label_prev_book)
                    pageRange.endInclusive < currentPage -> stringResource(
                        Res.string.book_label_next_book,
                    )
                    else -> stringResource(
                        Res.string.book_label_page_count,
                        currentPage,
                        pageRange.endInclusive.toInt(),
                    )
                },
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.size(ComicTheme.dimension.padding))
        HorizontalFloatingToolbar(
            expanded = false,
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                    ),
                ).padding(
                    start = ComicTheme.dimension.margin,
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin,
                ),
        ) {
            IconButton(
                onClick = { onPageChange(pageRange.endInclusive.toInt()) },
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                Icon(ComicIcons.FirstPage, null)
            }
            Spacer(Modifier.size(ComicTheme.dimension.padding))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                if (0 < pageRange.endInclusive.toInt()) {
                    Slider(
                        value = remember(currentPage, pageRange) {
                            currentPage
                                .coerceIn(
                                    pageRange.start.toInt(),
                                    pageRange.endInclusive.toInt(),
                                ).toFloat()
                        },
                        onValueChange = { onPageChange(it.toInt()) },
                        valueRange = pageRange,
                        steps = max((pageRange.endInclusive.toInt() / 2) - 2, 0),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.size(ComicTheme.dimension.padding))
            IconButton(
                onClick = { onPageChange(pageRange.start.toInt()) },
                modifier = Modifier.align(Alignment.CenterVertically),
            ) {
                Icon(ComicIcons.LastPage, null)
            }
        }
    }
}

@Composable
@Preview
private fun BookBottomBarPreview() {
    PreviewTheme {
        var currentPage by remember { mutableIntStateOf(50) }
        BookBottomBar(
            pageRange = 1f..100f,
            currentPage = currentPage,
            onPageChange = {
                currentPage = it
            },
        )
    }
}
