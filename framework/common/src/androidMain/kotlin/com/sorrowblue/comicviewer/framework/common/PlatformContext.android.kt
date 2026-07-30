/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.common

import android.content.Context

actual typealias PlatformContext = Context

actual fun <T> PlatformContext.appGraph(): T {
    @Suppress("UNCHECKED_CAST")
    return (applicationContext as AppGraphProvider<T>).appGraph
}
