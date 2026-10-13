/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.file.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.BookRibbon
import com.sorrowblue.comicviewer.framework.ui.component.file.FileInfoCardTitle
import com.sorrowblue.comicviewer.framework.ui.component.file.FileInfoDataText
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

@Composable
internal fun ReadingInsight(book: Book) {
    val isRead = book.totalPageCount > 0 && book.lastReadTime > 0L
    val isCompleted = isRead && book.lastPageRead + 1 >= book.totalPageCount
    OutlinedCard {
        Column(modifier = Modifier.padding(32.dp)) {
            FileInfoCardTitle(
                ComicIcons.BookRibbon,
                title = {
                    Text("Reading Insight")
                },
            )
            Spacer(modifier = Modifier.size(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FileInfoDataText(
                    overlineContent = { Text(text = "PROGRESS") },
                    headlineContent = {
                        val progressText = when {
                            !isRead -> "-"

                            isCompleted -> "100%"

                            else -> "${((book.lastPageRead + 1).toFloat() / book.totalPageCount * 100).toInt().coerceIn(
                                0,
                                100,
                            )}%"
                        }
                        Text(text = progressText)
                    },
                )
                Spacer(Modifier.weight(1f))
                FileInfoDataText(
                    overlineContent = { Text(text = "LAST PAGE") },
                    headlineContent = {
                        val pageText = when {
                            !isRead -> "-"
                            else -> "Page ${minOf(book.lastPageRead + 1, book.totalPageCount)}"
                        }
                        Text(text = pageText)
                    },
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            val color = ProgressIndicatorDefaults.linearColor
            LinearProgressIndicator(
                progress = {
                    if (isRead) {
                        minOf((book.lastPageRead + 1).toFloat() / book.totalPageCount, 1f)
                    } else {
                        0f
                    }
                },
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {
                    drawStopIndicator(
                        drawScope = this,
                        stopSize = 0.dp,
                        color = color,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(16.dp))
            FileInfoDataText(
                overlineContent = { Text(text = "LAST ACCESSED") },
                headlineContent = {
                    Text(text = if (book.lastReadTime == 0L) "-" else book.lastReadTime.asDateTime)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun ReadingInsightPreview() {
    PreviewTheme {
        ReadingInsight(book = fakeBookFile())
    }
}
