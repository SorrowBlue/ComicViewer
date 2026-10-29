/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.common.InternalDataApi
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * Unique identifier for a [Collection].
 *
 * @property value The underlying integer ID value.
 */
@Serializable
@JvmInline
value class CollectionId @InternalDataApi constructor(val value: Int) {
    companion object {
        /**
         * Creates a default uninitialized [CollectionId].
         */
        @OptIn(InternalDataApi::class)
        operator fun invoke() = CollectionId(0)
    }
}
