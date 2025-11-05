package com.sorrowblue.comicviewer.data.database

import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.data.database.di.TestAppGraph
import com.sorrowblue.comicviewer.data.database.entity.EntityFactory
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.createGraphFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import logcat.LogcatLogger
import logcat.PrintLogger

internal actual open class DatabaseTest {
    private lateinit var dbl: ComicViewerDatabase
    private lateinit var platformContextl: PlatformContext

    protected actual val platformContext: PlatformContext
        get() = platformContextl
    protected actual val db: ComicViewerDatabase
        get() = dbl
    protected actual val factory: EntityFactory = EntityFactory()

    @BeforeTest
    actual fun setupDatabase() {
        val graph = createGraphFactory<TestAppGraph.Factory>()
            .createAndroidAppGraph(InstrumentationRegistry.getInstrumentation().context)
        dbl = graph.database
        platformContextl = graph.platformContext
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    @AfterTest
    actual fun closeDatabase() {
        db.close()
    }
}
