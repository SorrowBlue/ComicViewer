/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.file.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.file.File

interface FileInfoNavKey : NavKey {
    val fileKey: File.Key
    val isOpenFolderEnabled: Boolean
    val sceneKey: String
}
