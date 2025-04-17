package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.CollectionEditorFormData
import com.sorrowblue.comicviewer.framework.ui.material3.ButtonWithIcon
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_create
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Controller
import soil.form.compose.FormScope
import soil.form.compose.SubmissionControl
import soil.form.compose.rememberSubmissionRuleAutoControl

@Composable
internal fun FormScope<out CollectionEditorFormData>.CreateButton(
    modifier: Modifier = Modifier,
    control: SubmissionControl<out CollectionEditorFormData> = rememberSubmissionRuleAutoControl(),
) {
    Controller(control) {
        ButtonWithIcon(
            modifier = modifier,
            onClick = it.onSubmit,
            enabled = it.canSubmit && !it.isSubmitting,
            iconEnabled = it.isSubmitting,
            icon = {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = ButtonDefaults.buttonColors().contentColor,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        ) {
            Text(text = stringResource(Res.string.collection_editor_label_create))
        }
    }
}
