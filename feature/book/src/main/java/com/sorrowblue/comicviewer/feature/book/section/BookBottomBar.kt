package com.sorrowblue.comicviewer.feature.book.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import java.lang.Integer.max
import logcat.logcat

@Composable
internal fun BookBottomBar(
    isVisible: Boolean,
    currentPageIndex: Int,
    totalPage: Int,
    onCurrentPageChange: (Float) -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {

        val range = remember(totalPage) {
            object : ClosedFloatingPointRange<Float> {
                override fun lessThanOrEquals(a: Float, b: Float): Boolean = a == b
                override val endInclusive: Float get() = totalPage.toFloat()
                override val start: Float get() = 1f
            }
        }
        Column(
            Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                        elevation = 3.0.dp
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = AppMaterialTheme.dimens.margin),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider(
                modifier = Modifier.rotate(180f),
                value = if (currentPageIndex < 1) 1f else if (totalPage < currentPageIndex) totalPage.toFloat() else currentPageIndex.toFloat(),
                onValueChange = {
                    logcat { "onValueChange=$it" }
                    onCurrentPageChange(it)
                },
                valueRange = range,
                steps = max(totalPage - 2, 0)
            )
            Text(
                text =
                when {
                    currentPageIndex < 1 -> "前の本"
                    totalPage < currentPageIndex -> "次の本"
                    else -> "$currentPageIndex / $totalPage"
                },
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(AppMaterialTheme.dimens.spacer)
            )
        }
    }
}
