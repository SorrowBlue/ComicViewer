package com.sorrowblue.comicviewer.feature.favorite.create

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.favorite.create.generated.resources.Res
import comicviewer.feature.favorite.create.generated.resources.favorite_create_message_error
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun FavoriteCreateScreenPreview() {
    PreviewTheme {
        FavoriteCreateScreen(
            onDismissRequest = {},
            onSubmit = {},
            uiState = FavoriteCreateScreenUiState(stringResource(Res.string.favorite_create_message_error))
        )
    }
}
