package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings

class UpdateBookshelfSettingsRequest(val update: (BookshelfDisplaySettings) -> BookshelfDisplaySettings) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}
