package com.sorrowblue.comicviewer.file.section

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
import com.sorrowblue.comicviewer.file.component.FileInfoCardTitle
import com.sorrowblue.comicviewer.file.component.FileInfoDataText
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.composeicons.BookRibbon
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import kotlin.math.floor

@Composable
internal fun ReadingInsight(book: Book) {
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
                        Text(
                            text = "${
                                floor(
                                    book.lastPageRead.toFloat() / book.totalPageCount.toFloat() * 100,
                                ).toInt()
                            }%",
                        )
                    },
                )
                Spacer(Modifier.weight(1f))
                FileInfoDataText(
                    overlineContent = { Text(text = "LAST PAGE") },
                    headlineContent = {
                        Text(
                            text = if (book.lastPageRead <=
                                0
                            ) {
                                "-"
                            } else {
                                "Page ${book.lastPageRead + 1}"
                            },
                        )
                    },
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            val color = ProgressIndicatorDefaults.linearColor
            LinearProgressIndicator(
                progress = { book.lastPageRead.toFloat() / book.totalPageCount.toFloat() },
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
                headlineContent = { Text(text = "2024/01/15 22:15") },
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
