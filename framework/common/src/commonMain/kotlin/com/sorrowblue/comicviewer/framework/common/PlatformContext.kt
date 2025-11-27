package com.sorrowblue.comicviewer.framework.common

@Suppress("AbstractClassCanBeInterface")
expect abstract class PlatformContext

expect val PlatformContext.platformGraph: PlatformGraph

fun <T : Any> PlatformContext.require(): T {
    @Suppress("UNCHECKED_CAST")
    return this.platformGraph as T
}

fun <T : Any, V : Any> PlatformContext.withGraph(create: T.() -> V, block: V.() -> Unit) {
    @Suppress("UNCHECKED_CAST")
    with(create(this.platformGraph as T)) {
        block()
    }
}
