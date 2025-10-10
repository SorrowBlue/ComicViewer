package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.collection.editor.component.supportingText
import soil.form.compose.FormField
import soil.form.compose.hasError

@Composable
internal fun <T> DropdownMenuField(
    field: FormField<T>,
    value: @Composable T.() -> String,
    label: @Composable () -> Unit,
    menus: List<T>,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = true,
    text: @Composable (T) -> Unit = { Text(text = value(it)) },
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).then(
                if (fillMaxWidth) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier
                }
            ),
            value = value(field.value),
            isError = field.hasError,
            supportingText = field.supportingText(),
            enabled = field.isEnabled,
            readOnly = true,
            onValueChange = {},
            singleLine = true,
            label = { label() },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            menus.forEach { option ->
                DropdownMenuItem(
                    text = { text(option) },
                    onClick = {
                        field.onValueChange(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
