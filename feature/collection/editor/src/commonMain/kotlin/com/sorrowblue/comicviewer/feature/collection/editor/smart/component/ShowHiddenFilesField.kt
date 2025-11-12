package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.SwitchWithText
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_show_hidden_files
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Field
import soil.form.compose.Form

@Composable
internal fun Form<SmartCollectionForm>.ShowHiddenFilesField(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Field(
        name = ShowHiddenFilesField,
        selector = { it.searchCondition.showHidden },
        updater = { copy(searchCondition = searchCondition.copy(showHidden = it)) },
        enabled = enabled,
    ) { field ->
        SwitchWithText(
            text = {
                Text(
                    text = stringResource(Res.string.collection_editor_label_show_hidden_files),
                )
            },
            checked = field.value,
            onCheckedChange = field::onValueChange,
            thumbContent = {
                Icon(
                    imageVector = ComicIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            },
            enabled = field.isEnabled,
            modifier = modifier,
        )
    }
}

internal const val ShowHiddenFilesField = "ShowHiddenFilesField"
