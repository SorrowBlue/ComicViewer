/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import dev.zacsweers.metro.asContribution
import dev.zacsweers.metro.createGraphFactory
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir
import kotlin.random.Random
import kotlinx.coroutines.test.runTest

actual fun setupTest() {
    initFileKit(appId = "com.sorrowblue.comicviewer.test-${Random.nextInt()}")
}

actual fun tearDownTest(appGraph: AppGraph) {
    appGraph.asContribution<TestHelperContext.Factory>()
        .createTestHelperContext().testHelper.closeDatabase()
    runTest {
        FileKit.cacheDir.file.deleteRecursively()
        FileKit.filesDir.file.deleteRecursively()
    }
}

actual fun createAppGraph(): AppGraph {
    return JvmApplication().appGraph
}

private class JvmApplication : DesktopContext(), AppGraphProvider<AppGraph> {
    override val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(this)
    }
}
