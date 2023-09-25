package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.debugPlaceholder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileGrid(
    file: File,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier) {
        Column(
            Modifier.combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onLongClick = onLongClick,
                onClick = onClick
            )
        ) {
            AsyncImage(
                model = file,
                placeholder = debugPlaceholder()
                    ?: forwardingPainter(
                        rememberVectorPainter(if (file is Book) ComicIcons.Book else ComicIcons.Folder),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface)
                    ),
                error = forwardingPainter(
                    rememberVectorPainter(if (file is Book) ComicIcons.Book else ComicIcons.Folder),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface)
                ),
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = CardDefaults.elevatedShape
                    )
                    .clip(CardDefaults.elevatedShape)
            )
            if (file is Book && 0 < file.lastPageRead) {
                LinearProgressIndicator(
                    progress = file.lastPageRead.toFloat() / file.totalPageCount,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Text(
                file.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewFileGrid() {
    ComicTheme {
        FileGrid(
            file = FakeFile,
            onClick = {},
            onLongClick = {}
        )
    }
}

fun forwardingPainter(
    painter: Painter,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onDraw: DrawScope.(ForwardingDrawInfo) -> Unit = DefaultOnDraw,
): Painter = ForwardingPainter(painter, alpha, colorFilter, onDraw)

data class ForwardingDrawInfo(
    val painter: Painter,
    val alpha: Float,
    val colorFilter: ColorFilter?,
)

private class ForwardingPainter(
    private val painter: Painter,
    private var alpha: Float,
    private var colorFilter: ColorFilter?,
    private val onDraw: DrawScope.(ForwardingDrawInfo) -> Unit,
) : Painter() {

    private var info = newInfo()

    override val intrinsicSize get() = painter.intrinsicSize

    override fun applyAlpha(alpha: Float): Boolean {
        if (alpha != DefaultAlpha) {
            this.alpha = alpha
            this.info = newInfo()
        }
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        if (colorFilter != null) {
            this.colorFilter = colorFilter
            this.info = newInfo()
        }
        return true
    }

    override fun DrawScope.onDraw() = onDraw(info)

    private fun newInfo() = ForwardingDrawInfo(painter, alpha, colorFilter)
}

private val DefaultOnDraw: DrawScope.(ForwardingDrawInfo) -> Unit = { info ->
    with(info.painter) {
        draw(size, info.alpha, info.colorFilter)
    }
}
