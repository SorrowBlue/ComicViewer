package com.sorrowblue.comicviewer.framework.common

actual typealias PlatformContext = DesktopContext

actual val PlatformContext.platformGraph: PlatformGraph
    get() = platformGraph
