package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings

internal object DisplaySettingsSerializer :
    KOkioSerializer<DisplaySettings>(DisplaySettings.serializer()) {
    override val fileName = "displaySettings.pb"
    override val defaultValue = DisplaySettings()
}
