/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import kotlinx.serialization.Serializable

@Serializable
data class BasicCollectionCreateNavKey(
    val bookshelfId: BookshelfId = BookshelfId.Companion(),
    val path: PathString = "",
) : NavKey
