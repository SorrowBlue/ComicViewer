package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffoldExtraPaneScope
import com.sorrowblue.comicviewer.framework.ui.adaptive.ExtraPaneScaffold

internal sealed interface BookshelfInfoSheetWrapperUiState {
    data object Loading : BookshelfInfoSheetWrapperUiState
    data class Loaded(val bookshelfFolder: BookshelfFolder) : BookshelfInfoSheetWrapperUiState
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CanonicalScaffoldExtraPaneScope.BookshelfInfoSheetWrapper(
    bookshelfId: BookshelfId,
    content: @Composable (BookshelfFolder) -> Unit,
) {
    val state = rememberBookshelfInfoSheetWrapperState(
        bookshelfId = bookshelfId,
    )
    when (val uiState = state.uiState) {
        is BookshelfInfoSheetWrapperUiState.Loaded -> content(uiState.bookshelfFolder)
        BookshelfInfoSheetWrapperUiState.Loading -> LoadingScreen()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun CanonicalScaffoldExtraPaneScope.LoadingScreen() {
    ExtraPaneScaffold(title = {}, onCloseClick = {}) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator(
                polygons = LoadingIndicatorDefaults.DeterminateIndicatorPolygons
            )
        }
    }
}
