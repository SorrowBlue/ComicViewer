package com.sorrowblue.comicviewer.feature.collection.editor.smart.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.component.OutlinedTextField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.BookshelfField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.PeriodField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.RangeField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.ShowHiddenFilesField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.SortTypeField
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_error_must_not_be_blank
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_cancel
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_collection_name
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_search_keyword
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import soil.form.FormPolicy
import soil.form.compose.FieldControl
import soil.form.compose.Form
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.rule.notBlank

internal interface CollectionEditorFormData {
    val name: String
}

@Serializable
internal data class SmartCollectionEditorFormData(
    override val name: String = "",
    val bookshelfId: BookshelfId? = null,
    val searchCondition: SearchCondition = SearchCondition(),
) : CollectionEditorFormData

@Composable
internal fun SmartCollectionEditorForm(
    formData: SmartCollectionEditorFormData,
    bookshelfList: List<Bookshelf>,
    onCancel: () -> Unit,
    onSubmit: suspend (SmartCollectionEditorFormData) -> Unit,
    modifier: Modifier = Modifier,
) {
    Form(
        onSubmit = onSubmit,
        saver = kSerializableSaver<SmartCollectionEditorFormData>(),
        initialValue = formData,
        key = formData,
        policy = FormPolicy.Minimal,
        modifier = modifier
    ) {
        Column {
            OutlinedTextField(
                control = rememberCollectionNameControl(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.size(ComicTheme.dimension.minPadding))

            OutlinedTextField(control = rememberQueryControl(), modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.size(ComicTheme.dimension.minPadding))

            BookshelfField(bookshelfList = bookshelfList, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.size(ComicTheme.dimension.minPadding))

            RangeField(modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.size(ComicTheme.dimension.minPadding))

            PeriodField(modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.size(ComicTheme.dimension.minPadding))

            SortTypeField(modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.size(ComicTheme.dimension.minPadding))

            ShowHiddenFilesField(modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.size(ComicTheme.dimension.padding))

            Row(modifier = Modifier.align(Alignment.End)) {
                TextButton(onClick = onCancel) {
                    Text(text = stringResource(Res.string.collection_editor_label_cancel))
                }
                Spacer(Modifier.size(ComicTheme.dimension.padding))

                CreateButton()
            }
        }
    }
}

@Composable
private fun FormScope<SmartCollectionEditorFormData>.rememberCollectionNameControl(): FieldControl<String> {
    val errorMessage = stringResource(Res.string.collection_editor_error_must_not_be_blank)
    return rememberFieldRuleControl(
        name = stringResource(Res.string.collection_editor_label_collection_name),
        select = { this.name },
        update = { copy(name = it) },
    ) {
        notBlank { errorMessage }
    }
}

@Composable
private fun FormScope<SmartCollectionEditorFormData>.rememberQueryControl(): FieldControl<String> {
    val errorMessage = stringResource(Res.string.collection_editor_error_must_not_be_blank)
    return rememberFieldRuleControl(
        name = stringResource(Res.string.collection_editor_label_search_keyword),
        select = { this.searchCondition.query },
        update = { copy(searchCondition = searchCondition.copy(query = it)) },
    ) {
        notBlank { errorMessage }
    }
}

@Preview
@Composable
private fun SmartCollectionEditorFormPreview() {
    ComicTheme {
        Scaffold {
            SmartCollectionEditorForm(
                formData = SmartCollectionEditorFormData(),
                bookshelfList = emptyList(),
                onCancel = {},
                onSubmit = {}
            )
        }
    }
}
