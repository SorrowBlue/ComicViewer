package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.Settings

internal object SettingsSerializer : OkioKSerializer<Settings>(Settings.serializer()) {
    override val fileName = "settings.pb"
    override val defaultValue = Settings()
}
