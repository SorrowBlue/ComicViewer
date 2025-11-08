package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationKeySerializersModule
import com.sorrowblue.comicviewer.feature.book.navigation.BookKeySerializersModule
import com.sorrowblue.comicviewer.feature.book.navigation.ReceiveBookKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKeySerializersModule
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionKeySerializersModule
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryKeySerializersModule
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterKeySerializersModule
import com.sorrowblue.comicviewer.feature.search.navigation.SearchKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialKeySerializersModule
import com.sorrowblue.comicviewer.folder.navigation.SortTypeSelectKey
import com.sorrowblue.comicviewer.framework.ui.navigation.AppNavigationState
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import logcat.logcat

@Composable
fun rememberAppNavigationState(bookData: String?): AppNavigationState {
    val configuration = SavedStateConfiguration {
        this.serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(SortTypeSelectKey::class, SortTypeSelectKey.serializer())

                include(BookshelfKeySerializersModule)
                include(CollectionKeySerializersModule)
                include(ReadLaterKeySerializersModule)
                include(HistoryKeySerializersModule)

                include(SearchKeySerializersModule)
                include(SettingsKeySerializersModule)
                include(BookKeySerializersModule)

                include(AuthenticationKeySerializersModule)
                include(TutorialKeySerializersModule)
            }
        }
    }
    val backStack = bookData?.let {
        rememberNavBackStack(configuration, BookshelfKey.List, ReceiveBookKey(it))
    } ?: rememberNavBackStack(configuration, BookshelfKey.List)
    return remember {
        AppNavigationStateImpl()
    }.apply {
        this.currentBackStack = backStack
    }
}

private class AppNavigationStateImpl : AppNavigationState {
    override lateinit var currentBackStack: NavBackStack<NavKey>

    override fun addToBackStack(screenKey: ScreenKey) {
        currentBackStack.add(screenKey)
        logcat { "currentBackStack=${currentBackStack.joinToString { it.toString() }}" }
    }

    override fun onBackPressed() {
        logcat { "currentBackStack=${currentBackStack.joinToString { it.toString() }}" }
        currentBackStack.removeLastOrNull()
    }
}
