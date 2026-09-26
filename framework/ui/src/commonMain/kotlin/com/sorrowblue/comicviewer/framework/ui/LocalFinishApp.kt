/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal containing the action to finish or exit the application.
 */
val LocalFinishApp = staticCompositionLocalOf<() -> Unit> { {} }
