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
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm.Auth
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.AuthField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DomainField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.HostField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PasswordFieldView
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PathField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PortField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.UsernameFieldView
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.EditorDialog
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import soil.form.FormPolicy
import soil.form.compose.Controller
import soil.form.compose.Form
import soil.form.compose.rememberSubmissionRuleAutoControl

internal data class SmbEditScreenUiState(
    val form: SmbEditScreenForm,
    override val editMode: BookshelfEditMode,
) : BookshelfEditScreenUiState

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
        saver = kSerializableSaver<SmbEditScreenForm>(),
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
                            .semantics { contentDataType = ContentDataType.None }
                    )
                    HostField(
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                            .semantics { contentDataType = ContentDataType.None }
                    )

                    PortField(
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                            .semantics { contentDataType = ContentDataType.None }
                    )

                    PathField(
                        auth = formState.value.auth,
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                            .semantics { contentDataType = ContentDataType.None }
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing * 2)
                            .semantics { contentDataType = ContentDataType.None }
                    )

                    AuthField(
                        enabled = !submission.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimension.targetSpacing)
                            .semantics { contentDataType = ContentDataType.None }
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
                                    .semantics { contentDataType = ContentDataType.None }
                            )

                            UsernameFieldView(
                                enabled = !submission.isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dimension.targetSpacing)
                                    .semantics { contentType = ContentType.Username }
                            )

                            PasswordFieldView(
                                enabled = !submission.isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = dimension.targetSpacing)
                                    .semantics { contentType = ContentType.Password }
                            )
                        }
                    }
                }
            }
            if (isDialog) {
                EditorDialog(
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
