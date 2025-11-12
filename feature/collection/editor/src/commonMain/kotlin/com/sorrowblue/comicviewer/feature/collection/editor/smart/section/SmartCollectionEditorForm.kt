package com.sorrowblue.comicviewer.feature.collection.editor.smart.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.component.CollectionNameTextField2
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditorScreenUiState
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.BookshelfField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.PeriodField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.QueryField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.RangeField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.ShowHiddenFilesField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.SortTypeField
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.serialization.Serializable
import soil.form.compose.Form

@Serializable
internal data class SmartCollectionForm(
    val name: String = "",
    val bookshelfId: BookshelfId? = null,
    val searchCondition: SearchCondition = SearchCondition(),
)

@Composable
internal fun SmartCollectionEditorForm(
    form: Form<SmartCollectionForm>,
    uiState: SmartCollectionEditorScreenUiState,
    bookshelf: Map<BookshelfId?, String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.wrapContentHeight()) {
        form.CollectionNameTextField2(
            enabled = uiState.enabledForm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ComicTheme.dimension.minPadding),
        )

        form.QueryField(
            enabled = uiState.enabledForm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ComicTheme.dimension.minPadding),
        )

        form.BookshelfField(
            enabled = uiState.enabledForm,
            bookshelf = bookshelf,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ComicTheme.dimension.minPadding),
        )

        form.RangeField(
            enabled = uiState.enabledForm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ComicTheme.dimension.minPadding),
        )

        form.PeriodField(
            enabled = uiState.enabledForm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ComicTheme.dimension.minPadding),
        )

        form.SortTypeField(
            enabled = uiState.enabledForm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ComicTheme.dimension.minPadding),
        )

        form.ShowHiddenFilesField(
            enabled = uiState.enabledForm,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ComicTheme.dimension.minPadding),
        )
    }
}
