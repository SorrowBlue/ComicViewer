package com.sorrowblue.comicviewer.framework.ui.navigation3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.LocalNavigator
import kotlin.reflect.KClass

class SupportingPaneExt<T : NavKey>(val clazz: KClass<T>)

const val TransitionName = "com.sorrowblue.comicviewer.framework.ui.navigation3.transitionName"

fun transitionName(key: String) = mapOf(TransitionName to key)

inline fun <reified T : NavKey> SupportingPaneSceneStrategy.Companion.mainPane(sceneKey: String) =
    SupportingPaneSceneStrategy.mainPane(sceneKey) +
        mapOf("SupportingPaneExt" to SupportingPaneExt(T::class))

@Composable
fun <T : Any> rememberCustomNavEntryDecorator(
    directive: PaneScaffoldDirective,
): CustomNavEntryDecorator<T> = remember(directive) {
    CustomNavEntryDecorator(directive)
}

class CustomNavEntryDecorator<T : Any> internal constructor(directive: PaneScaffoldDirective) :
    NavEntryDecorator<T>(
        decorate = { entry ->
            val insets = if (directive.maxHorizontalPartitions == 1) {
                // Single pane
                WindowInsets()
            } else {
                // Two pane
                val isSupportingPaneShow =
                    (entry.metadata["SupportingPaneExt"] as? SupportingPaneExt<*>)?.let {
                        LocalNavigator.current.backStack.last()::class == it.clazz
                    } ?: false
                if (isSupportingPaneShow) {
                    WindowInsets.safeDrawing.only(WindowInsetsSides.End)
                } else {
                    WindowInsets()
                }
            }
            Box(modifier = Modifier.consumeWindowInsets(insets)) {
                entry.Content()
            }
        },
        onPop = { contentKey -> },
    )
