package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.entity.EntityFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import logcat.LogcatLogger
import logcat.PrintLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@KoinApplication
internal object TestApp

internal expect fun startTestApp()

internal abstract class DatabaseTest : KoinTest {

    protected val db: ComicViewerDatabase by inject()

    protected val factory: EntityFactory = EntityFactory()

    @BeforeTest
    fun setupDatabase() {
        startTestApp()
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    @AfterTest
    fun closeDatabase() {
        db.close()
        stopKoin()
    }
}
