package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.TestDatabaseHelper
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.FakeCryptUtil
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@DependencyGraph(AppScope::class)
internal interface TestAppGraph {
    val database: ComicViewerDatabase
    val platformContext: PlatformContext

    @IoDispatcher
    @Provides
    @Suppress("InjectDispatcher")
    private fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

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
    private fun FakeCryptUtil.bind(): CryptUtil = this

    @ContributesTo(AppScope::class)
    @DependencyGraph.Factory
    fun interface Factory {
        fun createAndroidAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}
