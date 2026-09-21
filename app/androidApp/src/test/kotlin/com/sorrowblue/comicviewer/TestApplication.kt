/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import android.app.Application
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

@Suppress("Registered")
internal class TestApplication :
    Application(),
    MetroApplication,
    AppGraphProvider<AppGraph> {

    init {
        runCatching {
            val field = MetroAppComponentFactory::class.java.getDeclaredField("metroApplication")
            field.isAccessible = true
            field.set(null, this)
        }
    }

    override val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(
            this,
        )
    }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph
}
