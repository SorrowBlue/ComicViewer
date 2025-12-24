package com.sorrowblue.comicviewer.framework.common

@Suppress("AbstractClassCanBeInterface")
expect abstract class PlatformContext

lateinit var getPlatformGraph: () -> PlatformGraph

fun <T : Any> PlatformContext.require(): T {
    @Suppress("UNCHECKED_CAST")
    return getPlatformGraph() as T
}

fun <T : Any, V : Any> PlatformContext.withGraph(create: T.() -> V, block: V.() -> Unit) {
    @Suppress("UNCHECKED_CAST")
    with(create(getPlatformGraph() as T)) {
        block()
    }
}
