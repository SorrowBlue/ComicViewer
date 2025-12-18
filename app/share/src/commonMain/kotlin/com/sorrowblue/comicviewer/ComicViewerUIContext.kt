package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@Scope
annotation class ComicViewerUIScope

@GraphExtension(ComicViewerUIScope::class)
interface ComicViewerUIContext {
    val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase
    val getNavigationHistoryUseCase: GetNavigationHistoryUseCase
    val navKeySubclassMap: Set<Pair<KClass<NavKey>, KSerializer<NavKey>>>
    val navigationKeys: Set<NavigationKey>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createComicViewerUIContext(): ComicViewerUIContext
    }
}

@Composable
context(context: PlatformContext)
fun rememberComicViewerUIContext(): ComicViewerUIContext = rememberRetained {
    context.require<ComicViewerUIContext.Factory>().createComicViewerUIContext()
}
