/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal object SecuritySettingsSerializer :
    OkioKSerializer<SecuritySettings>(SecuritySettings.serializer()) {
    override val fileName = "securitySettings.pb"
    override val defaultValue = SecuritySettings()
}
