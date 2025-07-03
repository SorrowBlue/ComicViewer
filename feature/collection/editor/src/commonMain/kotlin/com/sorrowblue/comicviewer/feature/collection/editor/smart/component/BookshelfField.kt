package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorFormData
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_all_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_bookshelf
import org.jetbrains.compose.resources.stringResource
import soil.form.compose.Form
import soil.form.compose.FormField
import soil.form.compose.rememberField

@Composable
internal fun BookshelfField(
    form: Form<SmartCollectionEditorFormData>,
    bookshelfList: List<Bookshelf>,
    modifier: Modifier = Modifier,
    field: FormField<BookshelfId?> = form.rememberBookshelfFieldControl(),
) {
    DropdownMenuField(
        field = field,
        value = {
            bookshelfList.firstOrNull { it.id == this }?.displayName
                ?: stringResource(Res.string.collection_editor_label_all_bookshelf)
        },
        menus = remember(bookshelfList) { bookshelfList.map { it.id } },
        modifier = modifier
    )
}

@Composable
private fun Form<SmartCollectionEditorFormData>.rememberBookshelfFieldControl(): FormField<BookshelfId?> {
    return rememberField(
        name = stringResource(Res.string.collection_editor_label_bookshelf),
        selector = { it.bookshelfId },
        updater = { copy(bookshelfId = it) },
    )
}
