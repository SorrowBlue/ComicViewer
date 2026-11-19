/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.startup

import com.sorrowblue.comicviewer.data.coil.BaseCoilInitializer
import com.sorrowblue.comicviewer.framework.startup.Initializer
import com.sorrowblue.comicviewer.framework.startup.LogcatInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlin.reflect.KClass

@ContributesIntoSet(AppScope::class)
internal class CoilInitializer :
    BaseCoilInitializer(),
    Initializer<Unit> {
    override fun create() {
        initialize()
    }

    override fun dependencies(): List<KClass<out Initializer<*>>> = listOf(LogcatInitializer::class)
}
