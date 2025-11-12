package androidx.compose.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun VerticalScrollbarBox(
    state: ScrollState,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    alignment: Alignment = Alignment.TopEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        val scrollbarAdapter = rememberScrollbarAdapter(state)
        VerticalScrollbar(
            adapter = scrollbarAdapter,
            style = LocalScrollbarStyle.current,
            modifier = Modifier
                .padding(padding)
                .align(alignment),
        )
    }
}

@Composable
fun ScrollbarBox(
    state: LazyListState,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    alignment: Alignment = Alignment.TopEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        val scrollbarAdapter = rememberScrollbarAdapter(state)
        VerticalScrollbar(
            adapter = scrollbarAdapter,
            style = LocalScrollbarStyle.current,
            modifier = Modifier
                .padding(padding)
                .align(alignment),
        )
    }
}

@Composable
fun ScrollbarBox(
    state: LazyGridState,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    scrollbarWindowInsets: WindowInsets = WindowInsets(),
    alignment: Alignment = Alignment.TopEnd,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        val scrollbarAdapter = rememberScrollbarAdapter(state)
        VerticalScrollbar(
            adapter = scrollbarAdapter,
            style = LocalScrollbarStyle.current,
            modifier = Modifier
                .windowInsetsPadding(scrollbarWindowInsets)
                .padding(padding)
                .align(alignment),
        )
    }
}

@Composable
fun AlertDialogDefaults.scrollbarStyle(): ScrollbarStyle =
    alertDialogScrollbarStyleCached ?: ScrollbarStyle(
        minimalHeight = 24.dp,
        thickness = 16.dp,
        shape = RoundedCornerShape(8.dp),
        hoverDurationMillis = 300,
        unhoverColor = ComicTheme.colorScheme.contentColorFor(containerColor).copy(alpha = 0.12f),
        hoverColor = ComicTheme.colorScheme.contentColorFor(containerColor).copy(alpha = 0.50f),
    ).also {
        alertDialogScrollbarStyleCached = it
    }

private var alertDialogScrollbarStyleCached: ScrollbarStyle? = null
