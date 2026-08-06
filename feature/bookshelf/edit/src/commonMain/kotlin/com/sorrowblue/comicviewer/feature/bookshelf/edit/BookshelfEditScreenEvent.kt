/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

internal sealed interface BookshelfEditScreenEvent {

    data object BackClick : BookshelfEditScreenEvent
    data object DismissRequest : BookshelfEditScreenEvent
    data object SubmitClick : BookshelfEditScreenEvent
}
