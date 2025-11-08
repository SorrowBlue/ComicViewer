package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.entity.EntityFactory
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.FakeCryptUtil
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createDynamicGraphFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlinx.coroutines.CoroutineDispatcher
import logcat.LogcatLogger
import logcat.PrintLogger

internal open class DatabaseTest {
    private val graph =
        createDynamicGraphFactory<TestAppGraph.Factory>(FakeBindings)
            .createTestAppGraph(createPlatformContext())
    protected val platformContext: PlatformContext = graph.platformContext
    protected val database: ComicViewerDatabase = graph.database
    protected val factory: EntityFactory = EntityFactory()

    @BeforeTest
    fun setupDatabase() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    @AfterTest
    fun closeDatabase() {
        database.close()
    }

    @BindingContainer
    object FakeBindings {
        @Provides
        private fun providesRoomDatabaseForTest(
            helper: TestDatabaseHelper,
            decryptedPasswordConverters: DecryptedPasswordConverters,
            @IoDispatcher dispatcher: CoroutineDispatcher,
        ): ComicViewerDatabase = helper
            .getDatabaseBuilder()
            .addMigrations(ComicViewerDatabase.ManualMigration7to8())
            .addTypeConverter(decryptedPasswordConverters)
            .setQueryCoroutineContext(dispatcher)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .build()

        @Binds
        private val FakeCryptUtil.bind: CryptUtil get() = this
    }
}
