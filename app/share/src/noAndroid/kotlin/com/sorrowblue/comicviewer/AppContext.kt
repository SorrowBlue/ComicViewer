package com.sorrowblue.comicviewer

import com.sorrowblue.comicviewer.framework.common.Initializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class AppContextScope

@GraphExtension(AppContextScope::class)
interface AppContext {
    val initializer: Set<Initializer<*>>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createAppContext(): AppContext
    }
}
