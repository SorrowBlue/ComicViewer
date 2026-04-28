package com.sorrowblue.comicviewer

import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.InitializerScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(InitializerScope::class)
interface InitializerContext {
    val initializer: Set<Initializer<*>>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createInitializerContext(): InitializerContext
    }
}
