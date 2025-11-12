package com.sorrowblue.comicviewer.feature.collection.editor.smart.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_all_bookshelf
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_bookshelf
import org.jetbrains.compose.resources.stringResource
import soil.form.annotation.InternalSoilFormApi
import soil.form.compose.Field
import soil.form.compose.Form

@OptIn(InternalSoilFormApi::class)
@Composable
internal fun Form<SmartCollectionForm>.BookshelfField(
    enabled: Boolean,
    bookshelf: Map<BookshelfId?, String>,
    modifier: Modifier = Modifier,
) {
    Field(
        name = BookshelfField,
        selector = { it.bookshelfId },
        updater = { copy(bookshelfId = it) },
        enabled = enabled,
    ) { field ->
        DropdownMenuField(
            field = field,
            label = {
                Text(stringResource(Res.string.collection_editor_label_bookshelf))
            },
            value = {
                bookshelf[this] ?: stringResource(Res.string.collection_editor_label_all_bookshelf)
            },
            menus = remember(bookshelf) { bookshelf.map { it.key } },
            modifier = modifier,
        )
    }
}

internal const val BookshelfField = "BookshelfField"
