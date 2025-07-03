package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.ui.material3.ButtonWithIcon
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_create
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form

@Composable
internal fun CreateButton(
    form: Form<*>,
    modifier: Modifier = Modifier,
) {
    ButtonWithIcon(
        modifier = modifier,
        onClick = form::handleSubmit,
        enabled = form.meta.canSubmit,
        iconEnabled = form.meta.canSubmit,
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
