package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

interface ScreenKey : NavKey

interface NavigationKey : ScreenKey {
    val title: String
        @Composable
        get

    val icon: ImageVector
    val order: Int
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : NavKey> KSerializer<T>.asEntry(): NavKeyEntry {
    return (T::class as KClass<NavKey>) to (this as KSerializer<NavKey>)
}
