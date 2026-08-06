/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditPage
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenEvent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SelectionList
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.layout.plus

internal fun EntryProviderScope<NavKey>.bookshelfTypeEntry(
    eventFlow: EventFlow<BookshelfEditScreenEvent>,
    onBack: () -> Unit,
    onTypeClick: (BookshelfType) -> Unit,
    contentPadding: PaddingValues,
) {
    entry<BookshelfEditPage.WizardSelection>(metadata = {
        metadata {
            transitionMaterialSharedAxisX()
        }
    }) {
        BookshelfTypeScreen(
            event = eventFlow,
            onBack = onBack,
            onTypeClick = onTypeClick,
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun BookshelfTypeScreen(
    event: EventFlow<BookshelfEditScreenEvent>,
    onBack: () -> Unit,
    onTypeClick: (BookshelfType) -> Unit,
    contentPadding: PaddingValues,
) {
    EventEffect(event) {
        when (it) {
            is BookshelfEditScreenEvent.BackClick -> onBack()
            is BookshelfEditScreenEvent.DismissRequest -> onBack()
            is BookshelfEditScreenEvent.SubmitClick -> {}
        }
    }
    val items = remember { BookshelfType.entries.toList() }
    SelectionList(
        items = items,
        onSourceClick = onTypeClick,
        contentPadding = contentPadding.plus(PaddingValues(top = 16.dp)),
        modifier = Modifier.testTag("BookshelfSelectionList"),
    )
}
