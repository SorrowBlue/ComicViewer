/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import okio.BufferedSource

internal object ViewerSettingsSerializer :
    OkioKSerializer<ViewerSettings>(ViewerSettings.serializer()) {
    override val fileName = "viewerSettings.pb"
    override val defaultValue = ViewerSettings()

    override suspend fun readFrom(source: BufferedSource): ViewerSettings {
        val settings = super.readFrom(source)
        return if (settings.readAheadPageCount < 1) {
            settings.copy(readAheadPageCount = 1)
        } else {
            settings
        }
    }
}
