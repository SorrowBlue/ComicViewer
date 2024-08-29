package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm.Auth
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.AuthField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DomainField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.HostField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PasswordFieldView
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PathField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PortField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.UsernameFieldView
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditDialog
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditScreen
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.isCompact
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo
import kotlinx.parcelize.Parcelize
import soil.form.FormPolicy
import soil.form.compose.Controller
import soil.form.compose.Form
import soil.form.compose.rememberSubmissionRuleAutoControl

internal data class SmbEditScreenUiState(
    val form: SmbEditScreenForm,
    override val editMode: BookshelfEditMode,
) : BookshelfEditScreenUiState

@Parcelize
internal data class SmbEditScreenForm(
    override val displayName: String = "",
    val host: String = "",
    val port: Int = 445,
    val path: String = "",
    val auth: Auth = Auth.Guest,
    val domain: String = "",
    val username: String = "",
    val password: String = "",
) : BookshelfEditForm {

    enum class Auth {
        Guest,
        UserPass,
    }

    override fun <T : BookshelfEditForm> update(displayName: String): T {
        @Suppress("UNCHECKED_CAST")
        return copy(displayName = displayName) as T
    }
}

@Composable
internal fun SmbEditScreen(
    isDialog: Boolean,
    uiState: SmbEditScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSubmit: suspend (SmbEditScreenForm) -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    Form(
        onSubmit = onSubmit,
        initialValue = uiState.form,
        policy = FormPolicy.Default
    ) {
        Controller(rememberSubmissionRuleAutoControl()) { submission ->
            val content = remember {
                movableContentWithReceiverOf<ColumnScope> {
                    val dimension = ComicTheme.dimension
                    DisplayNameField(
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                    )
                    HostField(
                        enabled= !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                    )

                    PortField(
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                    )

                    PathField(
                        auth = formState.value.auth,
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing * 2)
                    )

                    AuthField(
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                    )

                    AnimatedVisibility(
                        visible = formState.value.auth == Auth.UserPass,
                        enter = fadeIn() + expandIn(initialSize = { IntSize(it.width, 0) }),
                        exit = shrinkOut(targetSize = { IntSize(it.width, 0) }) + fadeOut(),
                    ) {
                        Column {
                            DomainField(
                                enabled = !submission.isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dimension.targetSpacing)
                            )

                            UsernameFieldView(
                                enabled = !submission.isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dimension.targetSpacing)
                            )

                            PasswordFieldView(
                                enabled = !submission.isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dimension.targetSpacing)
                            )
                        }
                    }
                }
            }
            if (isDialog) {
                EditDialog(
                    uiState = uiState,
                    onDismissRequest = onBackClick,
                    submission = submission,
                    scrollState = scrollState,
                    content = content
                )
            } else {
                EditScreen(
                    uiState = uiState,
                    onBackClick = onBackClick,
                    submission = submission,
                    scrollState = scrollState,
                    snackbarHostState = snackbarHostState,
                    content = content
                )
            }
        }
    }
}

@PreviewMultiScreen
@Composable
private fun PreviewSmbEditScreen() {
    PreviewTheme {
        val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
        SmbEditScreen(
            isDialog = !windowAdaptiveInfo.isCompact,
            uiState = SmbEditScreenUiState(
                form = SmbEditScreenForm(auth = Auth.UserPass),
                editMode = BookshelfEditMode.Register(BookshelfType.DEVICE)
            ),
            snackbarHostState = SnackbarHostState(),
            onBackClick = {},
            onSubmit = {}
        )
    }
}
