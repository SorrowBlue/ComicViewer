package com.sorrowblue.comicviewer.feature.favorite.create

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
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteNameField
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import comicviewer.feature.favorite.create.generated.resources.Res
import comicviewer.feature.favorite.create.generated.resources.cancel
import comicviewer.feature.favorite.create.generated.resources.favorite_create_label_create
import comicviewer.feature.favorite.create.generated.resources.favorite_create_title_new_favorite
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import soil.form.FormPolicy
import soil.form.compose.Controller
import soil.form.compose.Form
import soil.form.compose.rememberSubmissionRuleAutoControl

internal data class FavoriteCreateScreenUiState(
    val error: String = "",
)

@Serializable
data class FavoriteCreate(val bookshelfId: BookshelfId = BookshelfId(), val path: String = "")

interface FavoriteCreateScreenNavigator {
    fun navigateUp()
}

@Destination<FavoriteCreate>(style = DestinationStyle.Dialog::class)
@Composable
internal fun FavoriteCreateScreen(
    route: FavoriteCreate,
    navigator: FavoriteCreateScreenNavigator = koinInject(),
    state: FavoriteCreateScreenState = rememberFavoriteCreateScreenState(route),
) {
    FavoriteCreateScreen(
        uiState = state.uiState,
        onDismissRequest = navigator::navigateUp,
        onSubmit = state::onSubmit
    )
    EventEffect(state.events) {
        when (it) {
            FavoriteCreateScreenEvent.Success -> navigator.navigateUp()
        }
    }
}

@Composable
internal fun FavoriteCreateScreen(
    uiState: FavoriteCreateScreenUiState,
    onDismissRequest: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    Form(onSubmit = { onSubmit(it) }, initialValue = "", policy = FormPolicy.Default) {
        AlertDialog(
            title = { Text(text = stringResource(Res.string.favorite_create_title_new_favorite)) },
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
                        Text(text = stringResource(Res.string.favorite_create_label_create))
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
                    Text(text = stringResource(Res.string.cancel))
                }
            }
        )
    }
}
