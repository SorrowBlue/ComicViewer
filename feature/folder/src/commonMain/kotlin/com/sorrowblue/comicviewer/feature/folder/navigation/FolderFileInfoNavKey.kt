/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.folder.navigation

import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.file.nav.FileInfoNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class FolderFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = false
    override val sceneKey = SCENE_KEY_FOLDER
}
