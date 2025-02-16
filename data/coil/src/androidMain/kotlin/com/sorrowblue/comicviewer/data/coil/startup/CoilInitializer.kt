package com.sorrowblue.comicviewer.data.coil.startup

import android.content.Context
import android.graphics.Bitmap
import androidx.startup.Initializer
import coil3.ImageLoader
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import com.sorrowblue.comicviewer.data.coil.BaseCoilInitializer
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import org.koin.androix.startup.KoinInitializer

internal class CoilInitializer : BaseCoilInitializer(), Initializer<Unit> {

    override fun create(context: Context) = initialize()

    override fun ImageLoader.Builder.setup() {
        allowRgb565(true)
        bitmapConfig(Bitmap.Config.ARGB_8888)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(LogcatInitializer::class.java, KoinInitializer::class.java)
    }
}
