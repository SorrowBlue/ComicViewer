/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail

interface ThumbnailRepository {
    suspend fun load(fileThumbnail: FileThumbnail): Resource<Unit, Resource.SystemError>
}
