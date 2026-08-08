/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.framework.common.appGraph
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.manualFileKitCoreInitialization

actual fun setupTest() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    instrumentation.uiAutomation.executeShellCommand(
        "pm grant ${instrumentation.targetContext.packageName} ${Manifest.permission.ACCESS_LOCAL_NETWORK}"
    )
    FileKit.manualFileKitCoreInitialization(InstrumentationRegistry.getInstrumentation().context)
}

actual fun tearDownTest(appGraph: AppGraph) {
    // Do nothing
}

actual fun createAppGraph(): AppGraph {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context.applicationContext.appGraph<AppGraph>()
}

@Composable
actual fun AppContent(appGraph: AppGraph) {
    // Do nothing
}
