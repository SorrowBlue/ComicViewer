package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface BookshelfWizardNavKey : ScreenKey {

    @Serializable
    data object Selection : BookshelfWizardNavKey

    @Serializable
    data class Edit(val bookshelfId: BookshelfId, val bookshelfType: BookshelfType) :
        BookshelfWizardNavKey
}
