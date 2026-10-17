/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.database.TestHelper
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Scope
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
    (appGraph as TestAppGraph).asContribution<TestHelperContext.Factory>()
        .createTestHelperContext().testHelper.closeDatabase()
    runTest {
        FileKit.cacheDir.file.deleteRecursively()
        FileKit.filesDir.file.deleteRecursively()
    }
}

actual fun createAppGraph(): AppGraph {
    return JvmApplication().appGraph
}

@DependencyGraph(scope = AppScope::class)
internal interface TestAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createTestAppGraph(@Provides applicationContext: PlatformContext): TestAppGraph
    }
}

@Scope
annotation class TestScope

@GraphExtension(TestScope::class)
interface TestHelperContext {
    val testHelper: TestHelper

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createTestHelperContext(): TestHelperContext
    }
}

private class JvmApplication : DesktopContext(), AppGraphProvider<AppGraph> {
    override val appGraph: AppGraph by lazy {
        createGraphFactory<TestAppGraph.Factory>().createTestAppGraph(this)
    }
}
