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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.component.CollectionNameTextField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.BookshelfField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.PeriodField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.QueryField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.RangeField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.ShowHiddenFilesField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.SortTypeField
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_cancel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

internal interface CollectionEditorFormData {
    val name: String

    fun <T : CollectionEditorFormData> update(name: String): T
}

@Serializable
internal data class SmartCollectionEditorFormData(
    override val name: String = "",
    val bookshelfId: BookshelfId? = null,
    val searchCondition: SearchCondition = SearchCondition(),
) : CollectionEditorFormData {

    override fun <T : CollectionEditorFormData> update(name: String): T {
        @Suppress("UNCHECKED_CAST")
        return copy(name = name) as T
    }
}

@Composable
internal fun SmartCollectionEditorForm(
    formData: SmartCollectionEditorFormData,
    bookshelfList: List<Bookshelf>,
    onCancel: () -> Unit,
    onSubmit: (SmartCollectionEditorFormData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val formState = rememberFormState(
        initialValue = formData,
        saver = kSerializableSaver<SmartCollectionEditorFormData>(),
    )
    val form = rememberForm(state = formState, onSubmit = onSubmit)
    var name by remember { mutableStateOf(formData.name) }
    LaunchedEffect(formData.name) {
        if (name != formData.name) {
            name = formData.name
            formState.reset(formData)
        }
    }
    Column(modifier = modifier) {
        CollectionNameTextField(form = form, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(ComicTheme.dimension.minPadding))

        QueryField(form = form, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(ComicTheme.dimension.minPadding))

        BookshelfField(
            form = form,
            bookshelfList = bookshelfList,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(ComicTheme.dimension.minPadding))

        RangeField(form = form, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(ComicTheme.dimension.minPadding))

        PeriodField(form = form, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(ComicTheme.dimension.minPadding))

        SortTypeField(form = form, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(ComicTheme.dimension.minPadding))

        ShowHiddenFilesField(form = form, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.size(ComicTheme.dimension.padding))

        Row(modifier = Modifier.align(Alignment.End)) {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(Res.string.collection_editor_label_cancel))
            }
            Spacer(Modifier.size(ComicTheme.dimension.padding))

            CreateButton(form = form)
        }
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
