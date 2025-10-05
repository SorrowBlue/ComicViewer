package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditorForm
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.fixedColors
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_guest
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_label_username_password
import org.jetbrains.compose.resources.stringResource
import soil.form.FieldName
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.rememberField
import soil.form.compose.watch

@Composable
internal fun AuthField(
    form: Form<SmbEditorForm>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    field: FormField<SmbEditorForm.Auth> = form.rememberAuthField(enabled),
) {
    val list = remember { SmbEditorForm.Auth.entries }
    Column(modifier = modifier) {
        SingleChoiceSegmentedButtonRow {
            list.forEachIndexed { index, auth ->
                SegmentedButton(
                    selected = auth == field.value,
                    onClick = { field.onValueChange(auth) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = list.size),
                    enabled = field.isEnabled,
                    colors = SegmentedButtonDefaults.fixedColors()
                ) {
                    Text(
                        text = stringResource(
                            when (auth) {
                                SmbEditorForm.Auth.Guest -> Res.string.bookshelf_edit_label_guest
                                SmbEditorForm.Auth.UserPass -> Res.string.bookshelf_edit_label_username_password
                            }
                        ),
                    )
                }
            }
        }
        val error = form.watch { meta.fields[AuthField]?.error }
        error?.messages?.firstOrNull()?.let {
            Text(it, style = ComicTheme.typography.bodySmall, color = ComicTheme.colorScheme.error)
        }
    }
}

internal const val AuthField: FieldName = "Auth"

@Composable
private fun Form<SmbEditorForm>.rememberAuthField(enabled: Boolean): FormField<SmbEditorForm.Auth> {
    return rememberField(
        name = AuthField,
        selector = { it.auth },
        updater = { copy(auth = it) },
        enabled = enabled
    )
}
