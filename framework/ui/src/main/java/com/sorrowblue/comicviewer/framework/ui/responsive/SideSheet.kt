package com.sorrowblue.comicviewer.framework.ui.responsive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeBottom
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeTop
import com.sorrowblue.comicviewer.framework.ui.copy

@Composable
fun <T : Any> rememberSideSheetValueState(
    initialValue: T? = null,
): SideSheetValueState<T> {
    return remember {
        SideSheetValueState(initialValue)
    }
}

@Stable
class SideSheetValueState<T : Any>(
    initialValue: T? = null,
    initialShow: Boolean = false,
) {
    fun show(value: T) {
        currentValue = value
        show = true
    }

    fun hide() {
        show = false
    }

    var currentValue by mutableStateOf(initialValue)
        private set

    var show by mutableStateOf(initialShow)
        private set
}

@Composable
fun rememberSideSheetBooleanState(
    initialValue: Boolean = false,
): SideSheetValueState<Boolean> {
    return rememberSaveable(
        saver =
        androidx.compose.runtime.saveable.Saver(
            save = { it.currentValue },
            restore = { savedValue ->
                SideSheetValueState(savedValue)
            }
        )
    ) {
        SideSheetValueState(initialValue)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = ComicTheme.colorScheme.surfaceContainerLow,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        windowInsets = WindowInsets(0),
        modifier = modifier,
        content = content
    )
}

@Composable
fun SideSheet(
    title: String,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(),
    width: Dp = SideSheetDefault.MinWidth,
    onCloseClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(innerPadding.copy(bottom = 0.dp, start = 0.dp))
            .padding(
                end = ComicTheme.dimension.margin
            )
            .width(width)
            .clip(ComicTheme.shapes.largeTop)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(ComicTheme.colorScheme.surface)
                .padding(
                    start = SideSheetDefault.Padding * 2,
                    top = SideSheetDefault.Padding,
                    end = SideSheetDefault.Padding,
                    bottom = SideSheetDefault.Padding
                )
        ) {
            Text(
                text = title,
                style = ComicTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = SideSheetDefault.Padding)
            )
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = ComicIcons.Close,
                    contentDescription = "Close"
                )
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .clip(ComicTheme.shapes.largeBottom)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = innerPadding.calculateBottomPadding() + ComicTheme.dimension.margin)
                    .clip(ComicTheme.shapes.largeBottom)
                    .background(ComicTheme.colorScheme.surface)
                    .padding(
                        start = SideSheetDefault.Padding,
                        top = ComicTheme.dimension.padding * 2,
                        end = SideSheetDefault.Padding,
                        bottom = SideSheetDefault.Padding
                    ),
            ) {
                content()
            }
        }
    }
}

object SideSheetDefault {
    val Padding = 12.dp
    val MinWidth = 256.dp
    val MaxWidth = 400.dp
}
