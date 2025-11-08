package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range_in_folder
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range_sub_folder
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Field
import soil.form.compose.Form

@Composable
internal fun Form<SmartCollectionForm>.RangeField(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Field(
        name = RangeField,
        selector = { it.searchCondition.range },
        updater = { copy(searchCondition = searchCondition.copy(range = it)) },
        enabled = enabled,
    ) { field ->
        DropdownMenuField(
            field = field,
            label = { Text(stringResource(Res.string.collection_editor_label_range)) },
            value = { stringResource(displayText) },
            menus = remember { SearchCondition.Range.entries },
            modifier = modifier,
        )
    }
}

internal const val RangeField = "RangeField"

private val SearchCondition.Range.displayText: StringResource
    get() = when (this) {
        SearchCondition.Range.Bookshelf -> Res.string.collection_editor_label_range_bookshelf
        is SearchCondition.Range.InFolder -> Res.string.collection_editor_label_range_in_folder
        is SearchCondition.Range.SubFolder -> Res.string.collection_editor_label_range_sub_folder
    }
