/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication.nav

import kotlinx.serialization.Serializable

@Serializable
enum class ScreenType {
    Register,
    Change,
    Erase,
    Authenticate,
}
