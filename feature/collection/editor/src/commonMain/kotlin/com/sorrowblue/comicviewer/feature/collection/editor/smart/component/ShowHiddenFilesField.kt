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
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope

@Composable
internal fun FormScope<SmartCollectionEditorFormData>.ShowHiddenFilesField(
    modifier: Modifier = Modifier,
    control: FieldControl<Boolean> = rememberShowHiddenFilesControl(),
) {
    Controller(control) { field ->
        SwitchWithText(
            text = { Text(text = field.name) },
            checked = field.value,
            onCheckedChange = field.onChange,
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
}

@Composable
private fun FormScope<SmartCollectionEditorFormData>.rememberShowHiddenFilesControl(): FieldControl<Boolean> {
    return rememberFieldControl(
        name = stringResource(Res.string.collection_editor_label_show_hidden_files),
        select = { searchCondition.showHidden },
        update = { copy(searchCondition = searchCondition.copy(showHidden = it)) }
    )
}
