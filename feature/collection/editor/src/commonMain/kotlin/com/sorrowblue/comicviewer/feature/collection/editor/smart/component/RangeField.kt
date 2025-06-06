package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range_in_folder
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_range_sub_folder
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.FieldControl
import soil.form.compose.FormScope

@Composable
internal fun FormScope<SmartCollectionEditorFormData>.RangeField(
    modifier: Modifier = Modifier,
    control: FieldControl<SearchCondition.Range> = rememberRangeControl(),
) {
    DropdownMenuField(
        control = control,
        value = { stringResource(displayText) },
        menus = remember { SearchCondition.Range.entries },
        modifier = modifier
    )
}

@Composable
private fun FormScope<SmartCollectionEditorFormData>.rememberRangeControl(): FieldControl<SearchCondition.Range> {
    return rememberFieldControl(
        name = stringResource(Res.string.collection_editor_label_range),
        select = { searchCondition.range },
        update = { copy(searchCondition = searchCondition.copy(range = it)) },
    )
}

private val SearchCondition.Range.displayText: StringResource
    get() = when (this) {
        SearchCondition.Range.Bookshelf -> Res.string.collection_editor_label_range_bookshelf
        is SearchCondition.Range.InFolder -> Res.string.collection_editor_label_range_in_folder
        is SearchCondition.Range.SubFolder -> Res.string.collection_editor_label_range_sub_folder
    }
