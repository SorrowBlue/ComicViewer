package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import io.github.takahirom.rin.rememberRetained
import kotlin.jvm.JvmSuppressWildcards
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
inline fun <reified T : NavKey> toPair(
    serializer: KSerializer<T>,
): Pair<KClass<NavKey>, KSerializer<NavKey>> =
    (T::class as KClass<NavKey>) to (serializer as KSerializer<NavKey>)

inline fun <reified T : NavKey, V : ScreenContext> EntryProviderScope<NavKey>.entryScreen(
    noinline createContext: () -> V,
    noinline clazzContentKey: (key: @JvmSuppressWildcards T) -> Any = {
        it::class.qualifiedName ?: it.toString()
    },
    metadata: Map<String, Any> = emptyMap(),
    noinline content: @Composable V.(T) -> Unit,
) {
    entry<T>(
        clazzContentKey = clazzContentKey,
        metadata = metadata,
    ) {
        with(rememberRetained { createContext() }) {
            content(it)
        }
    }
}
