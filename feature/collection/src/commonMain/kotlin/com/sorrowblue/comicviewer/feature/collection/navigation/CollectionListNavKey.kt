/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.BasicCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.SmartCollectionEditNavKey
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenRoot
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_title
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.binding
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@ContributesIntoSet(AppScope::class, binding = binding<NavigationKey>())
@Serializable
internal data object CollectionListNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.collection_title)

    override val icon get() = ComicIcons.CollectionsBookmark

    override val order get() = 2
}

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun collectionListNavEntry(navigator: Navigator) {
    scope.entry<CollectionListNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
        CollectionListScreenRoot(
            onItemClick = { collection -> navigator.navigate(CollectionNavKey(collection.id)) },
            onEditClick = { collection ->
                navigator.navigate(
                    when (collection) {
                        is BasicCollection ->
                            BasicCollectionEditNavKey(collection.id)

                        is SmartCollection ->
                            SmartCollectionEditNavKey(collection.id)
                    },
                )
            },
            onDeleteClick = { collection ->
                navigator.navigate(CollectionDeleteNavKey(collection.id))
            },
            onSettingsClick = {
                navigator.navigate(SettingsNavKey)
            },
            onCreateBasicCollectionClick = {
                navigator.navigate(BasicCollectionCreateNavKey())
            },
            onCreateSmartCollectionClick = {
                navigator.navigate(SmartCollectionCreateNavKey())
            },
        )
    }
}
