package com.sorrowblue.comicviewer.framework.common

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

actual typealias PlatformContext = IosContext

actual val PlatformContext.platformGraph: PlatformGraph
    get() = IosContext.platformGraph

abstract class IosContext {

    companion object {
        lateinit var platformGraph: PlatformGraph
    }
}

@ContributesBinding(AppScope::class, binding = binding<PlatformContext>())
@Inject
class IosContextImpl : IosContext()
