package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_title
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
internal data object ReadLaterNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.readlater_title)
    override val icon get() = ComicIcons.WatchLater

    override val order get() = 3
}

context(factory: ReadLaterScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.readLaterNavEntry(navigator: Navigator) {
    entry<ReadLaterNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane("ReadLater") +
            NavDisplay.transitionMaterialFadeThrough(),
    ) {
        with(rememberRetained { factory.createReadLaterScreenContext() }) {
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
                            navigator.navigate<ReadLaterFileInfoNavKey>(
                                ReadLaterFolderNavKey(
                                    bookshelfId = file.bookshelfId,
                                    path = file.path,
                                ),
                                inclusive = true,
                            )
                        }
                    }
                },
                onFileInfoClick = {
                    navigator.navigate<ReadLaterFileInfoNavKey>(
                        ReadLaterFileInfoNavKey(it.key()),
                        inclusive = true,
                    )
                },
            )
        }
    }
}
