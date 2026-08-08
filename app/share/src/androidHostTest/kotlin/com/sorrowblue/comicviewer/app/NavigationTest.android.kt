/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable

actual fun setupTest() {
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")
}

actual fun tearDownTest(appGraph: AppGraph) {
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")
}

actual fun createAppGraph(): AppGraph {
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")
}

@Composable
actual fun AppContent(appGraph: AppGraph) {
    throw NotImplementedError("AndroidUnit tests are not executed, so do nothing")
}
