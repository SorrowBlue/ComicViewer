/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import kotlinx.coroutines.Deferred

interface ThumbnailRepository {
    fun load(fileThumbnail: FileThumbnail): Deferred<Any>
}
