/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.startup

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metro.OnGraphInit

/**
 * Graph lifecycle callbacks for running startup [Initializer]s when the graph is created.
 */
@OptIn(ExperimentalMetroApi::class)
@ContributesTo(AppScope::class)
interface StartupGraphLifecycle {

    /**
     * Executes all contributed [Initializer]s in dependency order upon graph initialization.
     *
     * @param initializers Provider of contributed startup initializers.
     */
    @OnGraphInit
    fun initializeStartup(initializers: () -> Set<Initializer<*>>) {
        Initializer.initialize(initializers().toList())
    }
}
