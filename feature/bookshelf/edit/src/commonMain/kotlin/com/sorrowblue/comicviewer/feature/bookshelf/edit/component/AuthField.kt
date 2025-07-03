package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.bookshelf.edit.SmbEditScreenForm
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
    form: Form<SmbEditScreenForm>,
    modifier: Modifier = Modifier,
    field: FormField<SmbEditScreenForm.Auth> = form.rememberAuthField(),
) {
    val list = remember { SmbEditScreenForm.Auth.entries }
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
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
                            SmbEditScreenForm.Auth.Guest -> Res.string.bookshelf_edit_label_guest
                            SmbEditScreenForm.Auth.UserPass -> Res.string.bookshelf_edit_label_username_password
                        }
                    ),
                )
            }
        }
    }
}

internal const val AuthField: FieldName = "Auth"

@Composable
private fun Form<SmbEditScreenForm>.rememberAuthField(): FormField<SmbEditScreenForm.Auth> {
    return rememberField(
        name = AuthField,
        selector = { it.auth },
        updater = { copy(auth = it) },
        enabled = watch { !value.isRunning }
    )
}
