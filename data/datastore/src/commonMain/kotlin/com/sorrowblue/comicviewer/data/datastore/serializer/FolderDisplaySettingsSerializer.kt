package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings

internal object FolderDisplaySettingsSerializer :
    OkioKSerializer<FolderDisplaySettings>(FolderDisplaySettings.serializer()) {
    override val fileName = "folder_display_settings.pb"
    override val defaultValue = FolderDisplaySettings()
}
