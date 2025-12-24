package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenResultKey
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.HistoryScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.mainPane
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import io.github.takahirom.rin.rememberRetained

context(factory: HistoryScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.historyNavEntry(navigator: Navigator) {
    entry<HistoryNavKey>(
        metadata = SupportingPaneSceneStrategy.mainPane<HistoryFileInfoNavKey>("History") +
            NavigationResultMetadata.resultConsumer(ClearAllHistoryScreenResultKey) +
            NavDisplay.transitionMaterialFadeThrough(),
    ) {
        with(rememberRetained { factory.createHistoryScreenContext() }) {
            HistoryScreenRoot(
                onDeleteAllClick = {
                    navigator.navigate(HistoryClearAllNavKey)
                },
                onSettingsClick = { navigator.navigate(SettingsNavKey) },
                onBookClick = { book ->
                    navigator.navigate(
                        BookNavKey(
                            bookshelfId = book.bookshelfId,
                            path = book.path,
                            name = book.name,
                        ),
                    )
                },
                onBookInfoClick = {
                    navigator.navigate(HistoryFileInfoNavKey(it.key()))
                },
            )
        }
    }
}
