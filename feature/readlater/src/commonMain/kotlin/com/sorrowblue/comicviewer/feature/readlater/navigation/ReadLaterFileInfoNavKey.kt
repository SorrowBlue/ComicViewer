/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.file.nav.FileInfoNavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class ReadLaterFileInfoNavKey(override val fileKey: File.Key) : FileInfoNavKey {
    override val isOpenFolderEnabled: Boolean = true
    override val sceneKey = SCENE_KEY_READ_LATER
}
