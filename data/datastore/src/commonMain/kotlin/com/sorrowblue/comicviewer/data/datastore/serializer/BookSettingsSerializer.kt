package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.BookSettings

internal object BookSettingsSerializer :
    KOkioSerializer<BookSettings>(BookSettings.serializer()) {
    override val fileName = "bookSettings.pb"
    override val defaultValue = BookSettings()
}
