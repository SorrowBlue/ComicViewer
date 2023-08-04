package com.sorrowblue.comicviewer.folder.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Backdrop(
    frontLayer: @Composable ColumnScope.() -> Unit,
    toolbar: @Composable () -> Unit,
    backLayer: @Composable (PaddingValues) -> Unit,
    backdropScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    backLayerContainerColor: Color = BackdropDefaults.BackLayerContainerColor,
    backLayerContentColor: Color = BackdropDefaults.BackLayerContentColor,
) {
    Surface(
        color = backLayerContainerColor,
        contentColor = backLayerContentColor,
    ) {
        Column {
            toolbar()
            BottomSheetScaffold(
                scaffoldState = backdropScaffoldState,
                sheetPeekHeight = 200.dp,
                sheetContent = frontLayer,
                sheetSwipeEnabled = false,
                containerColor = backLayerContainerColor,
                contentColor = backLayerContentColor,
                sheetDragHandle = {},
                content = backLayer
            )
        }
    }
}

object BackdropDefaults {

    val BackLayerContainerColor
        @Composable
        get() = MaterialTheme.colorScheme.surfaceVariant
    val BackLayerContentColor
        @Composable
        get() = MaterialTheme.colorScheme.onSurfaceVariant
}
