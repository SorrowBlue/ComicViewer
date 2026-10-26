/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.common

/**
 * Marker annotation indicating that an API is intended for internal use within the data module.
 * Using an API annotated with this outside the data module requires `@OptIn(InternalDataApi::class)`.
 */
@RequiresOptIn(
    message = "This is an internal API for the data module. Do not use it in other modules.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalDataApi
