package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings

internal object FolderSettingsSerializer :
    OkioKSerializer<FolderSettings>(FolderSettings.serializer()) {
    override val fileName = "folder_settings.pb"
    override val defaultValue = FolderSettings()
}
