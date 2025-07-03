package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.SwitchWithText
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_show_hidden_files
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.rememberField

@Composable
internal fun ShowHiddenFilesField(
    form: Form<SmartCollectionEditorFormData>,
    modifier: Modifier = Modifier,
    field: FormField<Boolean> = form.rememberShowHiddenFilesField(),
) {
    SwitchWithText(
        text = { Text(text = field.name) },
        checked = field.value,
        onCheckedChange = field::onValueChange,
        thumbContent = {
            Icon(
                imageVector = ComicIcons.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        },
        modifier = modifier
    )
}

@Composable
private fun Form<SmartCollectionEditorFormData>.rememberShowHiddenFilesField(): FormField<Boolean> {
    return rememberField(
        name = stringResource(Res.string.collection_editor_label_show_hidden_files),
        selector = { it.searchCondition.showHidden },
        updater = { copy(searchCondition = searchCondition.copy(showHidden = it)) }
    )
}
