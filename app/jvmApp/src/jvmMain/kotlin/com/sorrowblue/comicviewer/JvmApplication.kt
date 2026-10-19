/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory

@DependencyGraph(scope = AppScope::class)
internal interface JvmAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createJvmAppGraph(@Provides applicationContext: PlatformContext): JvmAppGraph
    }
}

internal class JvmApplication :
    DesktopContext(),
    AppGraphProvider<AppGraph> {
    override val appGraph: AppGraph by lazy {
        createGraphFactory<JvmAppGraph.Factory>().createJvmAppGraph(this)
    }
}
