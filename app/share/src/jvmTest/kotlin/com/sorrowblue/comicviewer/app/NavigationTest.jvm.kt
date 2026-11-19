/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir
import kotlin.random.Random
import kotlinx.coroutines.test.runTest

actual fun setupTest() {
    initFileKit(appId = "com.sorrowblue.comicviewer.test-${Random.nextInt()}")
}

@OptIn(ExperimentalMetroApi::class)
actual fun tearDownTest(appGraph: AppGraph) {
    appGraph.close()
    runTest {
        FileKit.cacheDir.file.deleteRecursively()
        FileKit.filesDir.file.deleteRecursively()
    }
}

actual fun createAppGraph(): AppGraph = TestApplication().appGraph

@DependencyGraph(scope = AppScope::class)
internal interface TestAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createTestAppGraph(@Provides applicationContext: PlatformContext): TestAppGraph
    }
}

internal class TestApplication : JvmApplication() {
    override val appGraph: AppGraph by lazy {
        createGraphFactory<TestAppGraph.Factory>().createTestAppGraph(this)
    }
}
