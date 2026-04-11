package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.sorrowblue.comicviewer.feature.book.nav.BookNavKey
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenResultKey
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.HistoryScreenRoot
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.mainPane
import comicviewer.feature.history.generated.resources.Res
import comicviewer.feature.history.generated.resources.history_title
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
internal data object HistoryNavKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.history_title)

    override val icon get() = ComicIcons.History

    override val order get() = 4
}

context(factory: HistoryScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.historyNavEntry(navigator: Navigator) {
    entry<HistoryNavKey>(
        metadata = metadata {
            put(
                NavigationResultMetadata.ResultConsumerKey,
                NavigationResultMetadata.resultConsumer(ClearAllHistoryScreenResultKey)
            )
            transitionMaterialFadeThrough()
        } + SupportingPaneSceneStrategy.mainPane<HistoryFileInfoNavKey>("History"),
    ) {
        with(rememberRetained { factory.createHistoryScreenContext() }) {
            HistoryScreenRoot(
                onDeleteAllClick = {
                    navigator.navigate(HistoryClearAllNavKey)
                },
                onSettingsClick = {
                    navigator.navigate(SettingsNavKey)
                },
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
                    navigator.navigate<HistoryFileInfoNavKey>(
                        HistoryFileInfoNavKey(it.key()),
                        inclusive = true,
                    )
                },
            )
        }
    }
}
