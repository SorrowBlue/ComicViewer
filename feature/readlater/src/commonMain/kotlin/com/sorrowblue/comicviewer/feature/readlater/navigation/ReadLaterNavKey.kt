/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_title
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@ContributesIntoSet(AppScope::class)
@Serializable
internal data object ReadLaterNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.readlater_title)
    override val icon get() = ComicIcons.WatchLater

    override val order get() = 3
}

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun readLaterNavEntry(navigator: Navigator) {
    scope.entry<ReadLaterNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane("ReadLater") +
            NavDisplay.transitionMaterialFadeThrough(),
    ) {
        ReadLaterScreenRoot(
            onSettingsClick = {
                navigator.navigate(SettingsNavKey)
            },
            onFileClick = { file ->
                when (file) {
                    is Book -> {
                        navigator.navigate(
                            BookNavKey(
                                bookshelfId = file.bookshelfId,
                                path = file.path,
                                name = file.name,
                            ),
                        )
                    }

                    is Folder -> {
                        navigator.popNavigate<ReadLaterFileInfoNavKey>(
                            ReadLaterFolderNavKey(
                                bookshelfId = file.bookshelfId,
                                path = file.path,
                            ),
                        )
                    }
                }
            },
            onFileInfoClick = { file ->
                navigator.popNavigate<ReadLaterFileInfoNavKey>(
                    ReadLaterFileInfoNavKey(file.key()),
                )
            },
        )
    }
}
