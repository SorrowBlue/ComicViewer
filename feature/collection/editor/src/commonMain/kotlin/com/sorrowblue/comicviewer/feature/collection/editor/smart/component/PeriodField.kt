package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_hour24
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_month1
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_none
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_period_week1
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Field
import soil.form.compose.Form

@Composable
internal fun Form<SmartCollectionForm>.PeriodField(
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Field(
        name = PeriodField,
        selector = { it.searchCondition.period },
        updater = { copy(searchCondition = searchCondition.copy(period = it)) },
        enabled = enabled,
    ) { field ->
        DropdownMenuField(
            field = field,
            label = {
                Text(stringResource(Res.string.collection_editor_label_period))
            },
            value = { stringResource(displayText) },
            menus = remember { SearchCondition.Period.entries },
            modifier = modifier,
        )
    }
}

internal const val PeriodField = "PeriodField"

private val SearchCondition.Period.displayText: StringResource
    get() = when (this) {
        SearchCondition.Period.None -> Res.string.collection_editor_label_period_none
        SearchCondition.Period.Hour24 -> Res.string.collection_editor_label_period_hour24
        SearchCondition.Period.Week1 -> Res.string.collection_editor_label_period_week1
        SearchCondition.Period.Month1 -> Res.string.collection_editor_label_period_month1
    }
