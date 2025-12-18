package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenContext
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
internal data object BookMenuNavKey : ScreenKey

context(factory: BookMenuScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.bookMenuNavEntry(navigator: Navigator) {
    entry<BookMenuNavKey>(metadata = DialogSceneStrategy.dialog()) {
        with(rememberRetained { factory.createBookMenuScreenContext() }) {
            BookMenuScreenRoot(onDismissRequest = navigator::goBack)
        }
    }
}
