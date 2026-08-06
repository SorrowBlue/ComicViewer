/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface BookshelfEditPage : NavKey {

    @Serializable
    data object WizardSelection : BookshelfEditPage

    @Serializable
    data class WizardEdit(val editType: BookshelfEditType) : BookshelfEditPage

    @Serializable
    data class Discard(val force: Boolean) : BookshelfEditPage
}
