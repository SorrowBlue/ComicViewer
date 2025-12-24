package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.search.SearchScreenContext.Factory
import com.sorrowblue.comicviewer.feature.search.SearchScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: Factory)
internal fun EntryProviderScope<NavKey>.searchNavEntry(navigator: Navigator) {
    entry<SearchNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane("Search") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createSearchScreenContext() }) {
            SearchScreenRoot(
                bookshelfId = it.bookshelfId,
                path = it.path,
                onBackClick = navigator::goBack,
                onSettingsClick = { navigator.navigate(SettingsNavKey) },
                onSmartCollectionClick = { id, condition ->
                    navigator.navigate(SmartCollectionCreateNavKey(id, condition))
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
                            navigator.navigate(SearchFolderNavKey(file.bookshelfId, file.path))
                        }
                    }
                },
                onFileInfoClick = {
                    navigator.navigate(SearchFolderNavKey(it.bookshelfId, it.path))
                },
            )
        }
    }
}
