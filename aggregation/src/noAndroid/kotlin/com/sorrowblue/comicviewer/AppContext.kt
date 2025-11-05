package com.sorrowblue.comicviewer

import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext

interface AppContext {
    val platformContext: PlatformContext
    val initializer: Set<Initializer<*>>
}
