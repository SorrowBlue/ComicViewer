package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.material3.adaptive.navigation3.kmp.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenResultKey
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenRoot
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.HistoryScreenRoot
import com.sorrowblue.comicviewer.folder.navigation.FileInfoKey
import com.sorrowblue.comicviewer.folder.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import comicviewer.feature.history.generated.resources.Res
import comicviewer.feature.history.generated.resources.history_title
import io.github.irgaly.navigation3.resultstate.NavigationResultMetadata
import io.github.irgaly.navigation3.resultstate.resultConsumer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.stringResource

val HistoryKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(HistoryKey.List::class, HistoryKey.List.serializer())
        subclass(HistoryKey.FileInfo::class, HistoryKey.FileInfo.serializer())
        subclass(HistoryKey.ClearAll::class, HistoryKey.ClearAll.serializer())
    }
}

@Serializable
sealed interface HistoryKey : NavigationKey {
    override val title
        @Composable
        get() = stringResource(Res.string.history_title)
    override val icon get() = ComicIcons.History

    @Serializable
    data object List : HistoryKey

    @Serializable
    data class FileInfo(override val fileKey: File.Key) :
        HistoryKey,
        FileInfoKey {
        override val isOpenFolderEnabled: Boolean = true
    }

    @Serializable
    data object ClearAll : HistoryKey
}

context(graph: PlatformGraph, state: Navigation3State)
fun EntryProviderScope<NavKey>.historyEntryGroup(
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onCollectionClick: (File) -> Unit,
) {
    historyEntry(
        onSettingsClick = onSettingsClick,
        onDeleteAllClick = { state.addToBackStack(HistoryKey.ClearAll) },
        onBookClick = onBookClick,
        onBookInfoClick = { state.addToBackStack(HistoryKey.FileInfo(it.key())) },
    )
    historyClearAllEntry(
        onClose = state::onBackPressed,
    )
    historyFileInfoEntry(
        onBackClick = state::onBackPressed,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = {},
    )
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.historyEntry(
    onSettingsClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onBookInfoClick: (Book) -> Unit,
) {
    entryScreen<HistoryKey.List, HistoryScreenContext>(
        createContext = { (graph as HistoryScreenContext.Factory).createHistoryScreenContext() },
        metadata = SupportingPaneSceneStrategy.mainPane("History") +
            NavigationResultMetadata.resultConsumer(ClearAllHistoryScreenResultKey),
    ) {
        HistoryScreenRoot(
            onSettingsClick = onSettingsClick,
            onDeleteAllClick = onDeleteAllClick,
            onBookClick = onBookClick,
            onBookInfoClick = onBookInfoClick,
        )
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.historyClearAllEntry(onClose: () -> Unit) {
    entry<HistoryKey.ClearAll>(
        metadata = DialogSceneStrategy.dialog(),
    ) {
        ClearAllHistoryScreenRoot(onClose)
    }
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.historyFileInfoEntry(
    onBackClick: () -> Unit,
    onCollectionClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
) {
    fileInfoEntry<HistoryKey.FileInfo>(
        "History",
        onBackClick = onBackClick,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = onOpenFolderClick,
    )
}
