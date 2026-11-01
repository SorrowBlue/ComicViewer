/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable

actual fun setupTest(): Unit =
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")

actual fun tearDownTest(appGraph: AppGraph): Unit =
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")

actual fun createAppGraph(): AppGraph =
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")

@Composable
actual fun AppContent(appGraph: AppGraph): Unit =
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")
