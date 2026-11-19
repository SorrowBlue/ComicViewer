package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.di.DatabaseGraphLifecycle
import com.sorrowblue.comicviewer.data.database.entity.EntityFactory
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.FakeCryptUtil
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ExperimentalMetroApi
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
    protected val database: ComicViewerDatabase = graph.database
    protected val factory: EntityFactory = EntityFactory()

    @BeforeTest
    fun setupDatabase() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    @OptIn(ExperimentalMetroApi::class)
    @AfterTest
    fun closeDatabase() {
        graph.close()
    }

    @BindingContainer
    object FakeBindings {
        @Provides
        private fun providesRoomDatabaseForTest(
            helper: TestDatabaseHelper,
            decryptedPasswordConverters: DecryptedPasswordConverters,
            @IoDispatcher dispatcher: CoroutineDispatcher,
            holder: DatabaseGraphLifecycle.Holder,
        ): ComicViewerDatabase = holder.attach(
            helper
                .getDatabaseBuilder()
                .addMigrations(ComicViewerDatabase.ManualMigration7to8())
                .addColumnTypeConverter(decryptedPasswordConverters)
                .setQueryCoroutineContext(dispatcher)
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .build(),
        )

        @Binds
        private val FakeCryptUtil.bind: CryptUtil get() = this
    }
}
