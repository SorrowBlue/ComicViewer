package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.settings.folder.SortTypeScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
internal data class SortTypeNavKey(val sortType: SortType) : NavKey

internal fun EntryProviderScope<NavKey>.sortTypeNavEntry(navigator: Navigator) {
    entry<SortTypeNavKey>(metadata = DialogSceneStrategy.dialog()) {
        SortTypeScreenRoot(
            sortType = it.sortType,
            onDismissRequest = navigator::goBack,
        )
    }
}
