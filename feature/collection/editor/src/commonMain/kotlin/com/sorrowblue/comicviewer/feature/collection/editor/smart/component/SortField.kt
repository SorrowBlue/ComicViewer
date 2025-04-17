package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_sort
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_sort_date_asc
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_sort_date_desc
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_sort_name_asc
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_sort_name_desc
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_sort_size_asc
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_sort_size_desc
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.FieldControl
import soil.form.compose.FormScope

@Composable
internal fun FormScope<SmartCollectionEditorFormData>.SortTypeField(
    modifier: Modifier = Modifier,
    control: FieldControl<SortType> = rememberSortTypeControl(),
) {
    DropdownMenuField(
        control = control,
        value = { stringResource(displayText) },
        menus = remember { SortType.entries },
        modifier = modifier
    )
}

@Composable
private fun FormScope<SmartCollectionEditorFormData>.rememberSortTypeControl(): FieldControl<SortType> {
    return rememberFieldControl(
        name = stringResource(Res.string.collection_editor_label_sort),
        select = { searchCondition.sortType },
        update = { copy(searchCondition = searchCondition.copy(sortType = it)) },
    )
}

private val SortType.displayText: StringResource
    get() = when (this) {
        is SortType.Date -> if (isAsc) Res.string.collection_editor_label_sort_date_asc else Res.string.collection_editor_label_sort_date_desc
        is SortType.Name -> if (isAsc) Res.string.collection_editor_label_sort_name_asc else Res.string.collection_editor_label_sort_name_desc
        is SortType.Size -> if (isAsc) Res.string.collection_editor_label_sort_size_asc else Res.string.collection_editor_label_sort_size_desc
    }
