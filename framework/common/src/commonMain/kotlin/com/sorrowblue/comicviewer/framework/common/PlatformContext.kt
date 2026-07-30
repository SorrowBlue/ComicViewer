/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.common

@Suppress("AbstractClassCanBeInterface")
expect abstract class PlatformContext

interface AppGraphProvider<T> {
    val appGraph: T
}

expect fun <T> PlatformContext.appGraph(): T
