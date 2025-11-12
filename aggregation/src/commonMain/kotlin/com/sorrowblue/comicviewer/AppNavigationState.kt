package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.sorrowblue.comicviewer.app.navigation.AppSerializersModule
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import logcat.logcat

@Composable
fun rememberNavigation3State(): Navigation3State {
    val configuration = SavedStateConfiguration {
        serializersModule = AppSerializersModule
    }
    val backStack = rememberNavBackStack(configuration, BookshelfKey.List)
    return remember {
        Navigation3StateImpl()
    }.apply {
        this.currentBackStack = backStack
    }
}

private class Navigation3StateImpl : Navigation3State {
    override lateinit var currentBackStack: NavBackStack<NavKey>

    override fun addToBackStack(screenKey: NavKey) {
        currentBackStack.add(screenKey)
        logcat { "currentBackStack=${currentBackStack.joinToString { it.toString() }}" }
    }

    override fun onBackPressed() {
        logcat { "currentBackStack=${currentBackStack.joinToString { it.toString() }}" }
        currentBackStack.removeLastOrNull()
    }
}
