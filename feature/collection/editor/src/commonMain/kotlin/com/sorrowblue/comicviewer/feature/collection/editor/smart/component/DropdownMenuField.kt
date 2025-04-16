package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import soil.form.compose.Controller
import soil.form.compose.FieldControl

@Composable
internal fun <T> DropdownMenuField(
    control: FieldControl<T>,
    value: @Composable T.() -> String,
    menus: List<T>,
    modifier: Modifier = Modifier,
    text: @Composable (T) -> Unit = { Text(text = value(it)) },
) {
    var expanded by remember { mutableStateOf(false) }
    Controller(control) { field ->
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                value = value(field.value),
                enabled = field.isEnabled,
                readOnly = true,
                onValueChange = {},
                singleLine = true,
                label = { Text(text = field.name) },
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
                            field.onChange(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
