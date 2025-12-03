package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenResultKey
import com.sorrowblue.comicviewer.feature.history.ClearAllHistoryScreenRoot
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.HistoryScreenRoot
import com.sorrowblue.comicviewer.folder.navigation.FileInfoKey
import com.sorrowblue.comicviewer.folder.navigation.fileInfoEntry
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
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

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.historyEntryGroup(
    navigator: Navigator,
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onCollectionClick: (File) -> Unit,
) {
    historyEntry(
        onSettingsClick = onSettingsClick,
        onDeleteAllClick = { navigator.navigate(HistoryKey.ClearAll) },
        onBookClick = onBookClick,
        onBookInfoClick = { navigator.navigate(HistoryKey.FileInfo(it.key())) },
    )
    historyClearAllEntry(
        onClose = navigator::goBack,
    )
    historyFileInfoEntry(
        onBackClick = navigator::goBack,
        onCollectionClick = onCollectionClick,
        onOpenFolderClick = {},
    )
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.historyEntry(
    onSettingsClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onBookInfoClick: (Book) -> Unit,
) {
    entryScreen<HistoryKey.List, HistoryScreenContext>(
        createContext = {
            context.require<HistoryScreenContext.Factory>().createHistoryScreenContext()
        },
        metadata = SupportingPaneSceneStrategy.mainPane("History") +
            NavigationResultMetadata.resultConsumer(ClearAllHistoryScreenResultKey) +
            NavDisplay.transitionMaterialFadeThrough(),
    ) {
        HistoryScreenRoot(
            onSettingsClick = onSettingsClick,
            onDeleteAllClick = onDeleteAllClick,
            onBookClick = onBookClick,
            onBookInfoClick = onBookInfoClick,
        )
    }
}

private fun EntryProviderScope<NavKey>.historyClearAllEntry(onClose: () -> Unit) {
    entry<HistoryKey.ClearAll>(metadata = DialogSceneStrategy.dialog()) {
        ClearAllHistoryScreenRoot(onClose)
    }
}

context(context: PlatformContext)
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
