/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Window settings serializer
 */
@ContributesBinding(AppScope::class)
internal object WindowSettingsSerializer : OkioKSerializer<WindowSettings>(
    WindowSettings.serializer(),
) {
    override val fileName = "window_settings.pb"
    override val defaultValue = WindowSettings()
}
