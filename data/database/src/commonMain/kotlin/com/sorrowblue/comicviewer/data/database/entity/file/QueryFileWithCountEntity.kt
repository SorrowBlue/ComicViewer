/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database.entity.file

import androidx.room3.Embedded
import com.sorrowblue.comicviewer.domain.model.file.File

internal class QueryFileWithCountEntity(
    @Embedded val fileEntity: FileEntity,
    private val count: Int,
) {
    fun toModel(): File = fileEntity.toModel(count)
}
