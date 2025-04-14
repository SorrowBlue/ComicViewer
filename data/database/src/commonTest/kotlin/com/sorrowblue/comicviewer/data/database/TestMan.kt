package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.di.configurationKoinForTest
import com.sorrowblue.comicviewer.data.database.di.getTestRoomDatabase
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import logcat.LogcatLogger
import logcat.PrintLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.includes
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

internal abstract class TestMan : KoinTest {

    protected val db: ComicViewerDatabase by inject()

    @BeforeTest
    fun setupDatabase() {
        startKoin {
            includes(configurationKoinForTest())
            modules(module {
                single { FakeCryptUtil() } bind CryptUtil::class
                single { getTestRoomDatabase(get(), get()) }
            })
        }
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
