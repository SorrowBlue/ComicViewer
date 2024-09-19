package com.sorrowblue.comicviewer.feature.favorite.create

import android.os.Parcelable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteNameField
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import kotlinx.parcelize.Parcelize
import soil.form.FormPolicy
import soil.form.compose.Controller
import soil.form.compose.Form
import soil.form.compose.rememberSubmissionRuleAutoControl

@Parcelize
internal data class FavoriteCreateDialogScreenUiState(
    val error: String = "",
) : Parcelable

data class FavoriteCreateDialogScreenArgs(
    val favoriteBooksToAdd: FavoriteBooksToAdd? = null,
)

@Parcelize
data class FavoriteBooksToAdd(val bookshelfId: BookshelfId, val path: String) : Parcelable

@Destination<ExternalModuleGraph>(
    navArgs = FavoriteCreateDialogScreenArgs::class,
    style = DestinationStyle.Dialog::class
)
@Composable
internal fun FavoriteCreateDialogScreen(
    navArgs: FavoriteCreateDialogScreenArgs,
    destinationsNavigator: DestinationsNavigator,
) {
    FavoriteCreateDialogScreen(
        destinationsNavigator = destinationsNavigator,
        state = rememberFavoriteCreateDialogScreenState(navArgs),
    )
}

@Composable
private fun FavoriteCreateDialogScreen(
    destinationsNavigator: DestinationsNavigator,
    state: FavoriteCreateDialogScreenState,
) {
    FavoriteCreateDialogScreen(
        uiState = state.uiState,
        onDismissRequest = destinationsNavigator::navigateUp,
        onSubmit = state::onSubmit
    )
    LaunchedEventEffect(state.event) {
        when (it) {
            FavoriteCreateDialogScreenEvent.Success -> destinationsNavigator.navigateUp()
        }
    }
}

@Composable
private fun FavoriteCreateDialogScreen(
    uiState: FavoriteCreateDialogScreenUiState,
    onDismissRequest: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    Form(
        onSubmit = { onSubmit(it) },
        initialValue = "",
        policy = FormPolicy.Default
    ) {
        AlertDialog(
            title = { Text(text = stringResource(R.string.favorite_create_title_new_favorite)) },
            text = {
                Column {
                    FavoriteNameField()
                    if (uiState.error.isNotEmpty()) {
                        Text(
                            text = uiState.error,
                            color = ComicTheme.colorScheme.error,
                            style = ComicTheme.typography.bodySmall
                        )
                    }
                }
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Controller(rememberSubmissionRuleAutoControl()) {
                    TextButton(onClick = it.onSubmit, enabled = it.canSubmit && !it.isSubmitting) {
                        Text(text = stringResource(R.string.favorite_create_label_create))
                        AnimatedVisibility(visible = it.isSubmitting) {
                            CircularProgressIndicator(
                                Modifier
                                    .padding(start = ButtonDefaults.IconSpacing)
                                    .size(24.dp)
                            )
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewFavoriteCreateDialogScreen() {
    PreviewTheme {
        FavoriteCreateDialogScreen(
            onDismissRequest = {},
            onSubmit = {},
            uiState = FavoriteCreateDialogScreenUiState(stringResource(id = R.string.favorite_create_message_error))
        )
    }
}
