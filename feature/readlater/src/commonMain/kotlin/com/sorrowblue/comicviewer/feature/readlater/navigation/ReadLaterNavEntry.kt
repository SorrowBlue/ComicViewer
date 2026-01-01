package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

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
