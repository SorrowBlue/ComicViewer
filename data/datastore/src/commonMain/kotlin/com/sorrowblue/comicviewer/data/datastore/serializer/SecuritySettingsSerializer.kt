package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings

internal object SecuritySettingsSerializer :
    OkioKSerializer<SecuritySettings>(SecuritySettings.serializer()) {
    override val fileName = "securitySettings.pb"
    override val defaultValue = SecuritySettings()
}
