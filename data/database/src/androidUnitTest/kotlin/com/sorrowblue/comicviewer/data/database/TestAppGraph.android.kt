package com.sorrowblue.comicviewer.data.database

import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.framework.common.PlatformContext

internal actual fun createPlatformContext(): PlatformContext =
    InstrumentationRegistry.getInstrumentation().context
