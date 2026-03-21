package com.sorrowblue.comicviewer.data.coil

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.size.Precision
import com.sorrowblue.comicviewer.data.coil.di.CoilContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import logcat.LogPriority
import logcat.logcat

internal open class BaseCoilInitializer {
    fun initialize(platformContext: PlatformContext) {
        with(platformContext.require<CoilContext.Factory>().createCoilContext()) {
            SingletonImageLoader.setSafe { context ->
                ImageLoader(context)
                    .newBuilder()
                    .components(componentRegistry)
                    .crossfade(true)
                    .precision(Precision.INEXACT)
                    .apply { setup() }
                    .build()
            }
        }
        logcat(LogPriority.INFO) { "Initialized coil." }
    }

    open fun ImageLoader.Builder.setup() {}
}
