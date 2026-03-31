package com.sorrowblue.comicviewer.framework.common

@Suppress("AbstractClassCanBeInterface")
expect abstract class PlatformContext

lateinit var getPlatformGraph: () -> PlatformGraph

fun <T : Any> PlatformContext.require(): T {
    @Suppress("UNCHECKED_CAST")
    return getPlatformGraph() as T
}
