package com.sorrowblue.comicviewer.framework.common

import android.content.Context

actual typealias PlatformContext = Context

actual val PlatformContext.platformGraph get() = (applicationContext as PlatformApplication).platformGraph

interface PlatformApplication {
    val platformGraph: PlatformGraph
}
