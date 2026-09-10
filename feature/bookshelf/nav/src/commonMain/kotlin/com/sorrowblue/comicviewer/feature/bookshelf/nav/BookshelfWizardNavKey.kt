/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import kotlinx.serialization.Serializable

@Serializable
sealed interface BookshelfWizardNavKey : NavKey {

    @Serializable
    data object Selection : BookshelfWizardNavKey

    @Serializable
    data class Edit(val bookshelfId: BookshelfId, val bookshelfType: BookshelfType) :
        BookshelfWizardNavKey
}
