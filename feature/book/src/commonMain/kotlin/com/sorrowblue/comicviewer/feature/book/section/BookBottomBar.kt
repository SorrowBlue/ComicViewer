package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_label_next_book
import comicviewer.feature.book.generated.resources.book_label_page_count
import comicviewer.feature.book.generated.resources.book_label_prev_book
import kotlin.math.max
import logcat.logcat
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
    LaunchedEffect(pageRange) {
        logcat { "pageRange = ${pageRange.start} ..< ${pageRange.endInclusive}" }
        logcat { "currentPage = $currentPage" }
    }
    Column(
        Modifier
            .background(
                MaterialTheme.colorScheme.surfaceColorAtElevation(
                    elevation = 3.0.dp
                )
            )
            .navigationBarsPadding()
            .padding(horizontal = ComicTheme.dimension.margin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sliderはページの範囲だけ
        Slider(
            modifier = Modifier.rotate(180f),
            value = remember(currentPage, pageRange) {
                currentPage.coerceIn(
                    pageRange.start.toInt(),
                    pageRange.endInclusive.toInt()
                ).toFloat()
            },
            onValueChange = {
                logcat { "onValueChange=$it" }
                onPageChange(it.toInt())
            },
            valueRange = pageRange,
            steps = max((pageRange.endInclusive.toInt() / 2) - 2, 0)
        )
        Text(
            text = when {
                currentPage < 1 -> stringResource(Res.string.book_label_prev_book)
                pageRange.endInclusive < currentPage -> stringResource(Res.string.book_label_next_book)
                else -> stringResource(
                    Res.string.book_label_page_count,
                    currentPage,
                    pageRange.endInclusive.toInt()
                )
            },
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(ComicTheme.dimension.spacer)
        )
    }
}
