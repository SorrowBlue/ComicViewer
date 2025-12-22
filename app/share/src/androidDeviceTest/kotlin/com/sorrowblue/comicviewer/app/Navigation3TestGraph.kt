package com.sorrowblue.comicviewer.app

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
interface Navigation3TestGraph : PlatformGraph {

    val context: PlatformContext
    val entries: Set<EntryProviderScope<NavKey>.(Navigator) -> Unit>

    val bookshelfLocalDataSource: BookshelfLocalDataSource
    val fileLocalDataSource: FileLocalDataSource
    val folderSettingsUseCase: ManageFolderSettingsUseCase
    val collectionLocalDataSource: CollectionLocalDataSource
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): Navigation3TestGraph
    }
}

object FakeBindings
