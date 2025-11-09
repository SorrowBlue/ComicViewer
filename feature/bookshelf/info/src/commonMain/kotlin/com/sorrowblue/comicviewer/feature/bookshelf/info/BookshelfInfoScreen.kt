package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.info.section.BottomActions
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffold
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfErrorContents(modifier: Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularProgressIndicator()
    }
}

internal sealed interface BookshelfInfoSheetUiState {
    data object Loading : BookshelfInfoSheetUiState

    data object Error : BookshelfInfoSheetUiState

    data class Loaded(val bookshelfFolder: BookshelfFolder) : BookshelfInfoSheetUiState
}

@Composable
internal fun BookshelfInfoScreen(
    uiState: BookshelfInfoSheetUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
    content: @Composable ColumnScope.(PaddingValues) -> Unit,
) {
    ExtraPaneScaffold(
        title = { Text(text = stringResource(Res.string.bookshelf_info_title)) },
        onCloseClick = onBackClick,
        actions = {
            BottomActions(
                onEditClick = onEditClick,
                onRemoveClick = onRemoveClick,
                enabled = uiState is BookshelfInfoSheetUiState.Loaded,
            )
        },
        content = content,
    )
}
