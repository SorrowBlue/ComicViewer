package com.sorrowblue.comicviewer.feature.favorite.common.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.common.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.debugPlaceholder

@Composable
fun FavoriteItem(favorite: Favorite, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
//            indication = rememberRipple(color = MaterialTheme.colorScheme.onSurface),
            indication = LocalIndication.current,
            onClick = onClick
        ),
        headlineContent = { Text(favorite.name) },
        supportingContent = {
            Text(
                pluralStringResource(
                    id = R.plurals.favorite_common_label_file_count,
                    count = favorite.count,
                    favorite.count
                )
            )
        },
        leadingContent = {
            AsyncImage(
                model = favorite,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                error = forwardingPainter(rememberVectorPainter(Icons.TwoTone.Image), colorFilter = ColorFilter.tint(ListItemDefaults.contentColor)),
                placeholder = debugPlaceholder()
            )
        }
    )
}

@Preview
@Composable
private fun PrivateFavoriteItem() {
    AppMaterialTheme {
        FavoriteItem(Favorite("Preview name"), {})
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