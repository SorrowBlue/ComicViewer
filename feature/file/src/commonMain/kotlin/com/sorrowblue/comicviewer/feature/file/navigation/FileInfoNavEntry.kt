/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.file.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionAddNavKey
import com.sorrowblue.comicviewer.feature.file.FileInfoScreenRoot
import com.sorrowblue.comicviewer.feature.file.nav.FileInfoNavKey
import com.sorrowblue.comicviewer.feature.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntryProvider
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlin.reflect.KClass

@ContributesIntoSet(AppScope::class)
internal class FileInfoNavEntry(val fileInfoNavKeySet: Set<KClass<out FileInfoNavKey>>) :
    NavigationEntryProvider {

    context(scope: EntryProviderScope<NavKey>)
    override fun invoke(navigator: Navigator) {
        fileInfoNavEntry(navigator, fileInfoNavKeySet)
    }
}

context(scope: EntryProviderScope<NavKey>)
private fun fileInfoNavEntry(
    navigator: Navigator,
    fileInfoNavKeySet: Set<KClass<out FileInfoNavKey>>,
) {
    fileInfoNavKeySet.forEach { clazz ->
        scope.addEntryProvider(
            clazz = clazz,
            metadata = { key ->
                SupportingPaneSceneStrategy.extraPane(key.sceneKey) +
                    NavDisplay.transitionMaterialSharedAxisX()
            },
        ) { navKey ->
            FileInfoScreenRoot(
                fileKey = navKey.fileKey,
                isOpenFolderEnabled = navKey.isOpenFolderEnabled,
                onBackClick = navigator::goBack,
                onCollectionClick = {
                    navigator.navigate(BasicCollectionAddNavKey(it.bookshelfId, it.path))
                },
                onOpenFolderClick = {
                    navigator.navigate(FolderNavKey(it.bookshelfId, it.parent, it.path))
                },
            )
        }
    }
}
