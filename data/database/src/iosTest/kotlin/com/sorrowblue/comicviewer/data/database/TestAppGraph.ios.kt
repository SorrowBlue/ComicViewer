package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.framework.common.IosContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext

internal actual fun createPlatformContext(): PlatformContext = TestIosContext()

class TestIosContext : IosContext()
