package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings

internal object ViewerSettingsSerializer :
    KOkioSerializer<ViewerSettings>(ViewerSettings.serializer()) {
    override val fileName = "viewerSettings.pb"
    override val defaultValue = ViewerSettings()
}
