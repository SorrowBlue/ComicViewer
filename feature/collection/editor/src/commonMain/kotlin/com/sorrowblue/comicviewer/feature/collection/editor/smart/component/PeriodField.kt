package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_hour24
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_month1
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_none
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_week1
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.FieldControl
import soil.form.compose.FormScope

@Composable
internal fun FormScope<SmartCollectionEditorFormData>.PeriodField(
    modifier: Modifier = Modifier,
    control: FieldControl<SearchCondition.Period> = rememberPeriodControl(),
) {
    DropdownMenuField(
        control = control,
        value = { stringResource(displayText) },
        menus = remember { SearchCondition.Period.entries },
        modifier = modifier
    )
}

@Composable
private fun FormScope<SmartCollectionEditorFormData>.rememberPeriodControl(): FieldControl<SearchCondition.Period> {
    return rememberFieldControl(
        name = stringResource(Res.string.collection_editor_label_period),
        select = { searchCondition.period },
        update = { copy(searchCondition = searchCondition.copy(period = it)) },
    )
}

private val SearchCondition.Period.displayText: StringResource
    get() = when (this) {
        SearchCondition.Period.None -> Res.string.collection_editor_label_period_none
        SearchCondition.Period.Hour24 -> Res.string.collection_editor_label_period_hour24
        SearchCondition.Period.Week1 -> Res.string.collection_editor_label_period_week1
        SearchCondition.Period.Month1 -> Res.string.collection_editor_label_period_month1
    }
