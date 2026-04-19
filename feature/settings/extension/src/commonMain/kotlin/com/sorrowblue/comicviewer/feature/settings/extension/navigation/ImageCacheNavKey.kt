package com.sorrowblue.comicviewer.feature.settings.extension.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.extension.subscreen.imagecache.ImageCacheScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
internal data object ImageCacheNavKey : NavKey

context(factory: ImageCacheScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.imageCacheNavEntry(navigator: Navigator) {
    entry<ImageCacheNavKey>(
        metadata = metadata {
            transitionMaterialSharedAxisX()
        } + ListDetailSceneStrategy.detailPane("Settings"),
    ) {
        with(rememberRetained { factory.createImageCacheScreenContext() }) {
            ImageCacheScreenRoot(onBackClick = navigator::goBack)
        }
    }
}
