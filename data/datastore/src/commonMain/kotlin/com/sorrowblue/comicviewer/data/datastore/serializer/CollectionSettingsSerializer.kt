/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.serializer

import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings

internal object CollectionSettingsSerializer :
    OkioKSerializer<CollectionSettings>(CollectionSettings.serializer()) {
    override val fileName = "collectionSettings.pb"
    override val defaultValue = CollectionSettings()
}
