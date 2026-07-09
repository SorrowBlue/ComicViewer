/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class BookshelfId @InternalDataApi constructor(val value: Int) {
    companion object {
        @OptIn(InternalDataApi::class)
        operator fun invoke() = BookshelfId(0)
    }
}
