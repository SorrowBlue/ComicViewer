/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.navigation

import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.file.nav.FileInfoNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class CollectionFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = true
    override val sceneKey = SCENE_KEY_COLLECTION
}
