/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.Serializable

/**
 * Represents the reading and binding direction of a comic book.
 */
@Serializable
enum class BindingDirection {
    /** Left-to-right binding direction. */
    LTR,

    /** Right-to-left binding direction (standard Japanese manga). */
    RTL,
}
