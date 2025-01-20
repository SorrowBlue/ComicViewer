package com.sorrowblue.comicviewer.feature.favorite.common.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.favorite.common.generated.resources.Res
import comicviewer.feature.favorite.common.generated.resources.favorite_common_label_favorite_name
import comicviewer.feature.favorite.common.generated.resources.favorite_common_message_error
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.FormScope
import soil.form.compose.onFocusChanged
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notBlank

@Composable
fun FormScope<String>.FavoriteNameField(
    modifier: Modifier = Modifier,
    control: FieldControl<String> = rememberNameFieldControl(),
) {
    Controller(control) { field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onChange,
            label = { Text(text = field.name) },
            modifier = modifier.onFocusChanged(field),
            enabled = field.isEnabled,
            isError = field.hasError,
            singleLine = true,
            supportingText = {
                if (field.hasError) {
                    Text(
                        text = field.errors.first(),
                        color = ComicTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
private fun FormScope<String>.rememberNameFieldControl(): FieldControl<String> {
    val notBlankMessage = stringResource(Res.string.favorite_common_message_error)
    return rememberFieldRuleControl(
        name = stringResource(Res.string.favorite_common_label_favorite_name),
        select = { this },
        update = { it }
    ) {
        notBlank { notBlankMessage }
    }
}
