/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal object SettingsSerializer : OkioKSerializer<Settings>(Settings.serializer()) {
    override val fileName = "settings.pb"
    override val defaultValue = Settings()
}
