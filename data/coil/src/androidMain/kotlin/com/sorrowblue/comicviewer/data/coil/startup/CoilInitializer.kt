/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.startup

import android.content.Context
import android.graphics.Bitmap
import androidx.startup.Initializer
import coil3.ImageLoader
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import com.sorrowblue.comicviewer.data.coil.BaseCoilInitializer
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.startup.LogcatInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

internal class CoilInitializer :
    BaseCoilInitializer(),
    Initializer<Unit> {
    override fun create(context: Context) {
        context.appGraph<CoilInitializerInjector>().inject(this)
        initialize()
    }

    override fun ImageLoader.Builder.setup() {
        allowRgb565(true)
        bitmapConfig(Bitmap.Config.ARGB_8888)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(LogcatInitializer::class.java)
}

@ContributesTo(AppScope::class)
interface CoilInitializerInjector {
    fun inject(target: BaseCoilInitializer)
}
