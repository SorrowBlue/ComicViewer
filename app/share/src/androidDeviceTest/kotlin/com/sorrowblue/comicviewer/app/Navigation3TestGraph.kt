package com.sorrowblue.comicviewer.app

import android.content.Context
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
interface Navigation3TestGraph {

    val bookshelfLocalDataSource: BookshelfLocalDataSource
    val fileLocalDataSource: FileLocalDataSource

    @Provides
    private fun provideWorkManager(context: Context): WorkManager = WorkManager.getInstance(context)

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): Navigation3TestGraph
    }
}

object FakeBindings
