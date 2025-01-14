package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings

internal object ViewerOperationSettingsSerializer :
    KOkioSerializer<ViewerOperationSettings>(ViewerOperationSettings.serializer()) {
    override val fileName = "viewerOperationSettings.pb"
    override val defaultValue = ViewerOperationSettings()
}
