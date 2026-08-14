/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.database.TestHelper
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Scope
import com.sorrowblue.comicviewer.domain.service.datasource.JvmDatastoreDataSource
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph


@DependencyGraph(scope = AppScope::class)
actual interface AppGraph :
    ViewModelGraph,
    PlatformGraph {
    actual val context: PlatformContext
    actual val viewModelFactory: MetroViewModelFactory
    val jvmDatastoreDataSource: JvmDatastoreDataSource

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createAppGraph(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): AppGraph
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
