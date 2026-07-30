/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.common

actual typealias PlatformContext = DesktopContext

actual fun <T> PlatformContext.appGraph(): T {
    @Suppress("UNCHECKED_CAST")
    return (this as AppGraphProvider<T>).appGraph
}
