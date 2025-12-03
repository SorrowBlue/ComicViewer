package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import logcat.logcat

val LocalNavigator = staticCompositionLocalOf<Navigator> {
    error("No AdaptiveNavigationSuiteState provided")
}

class Navigator private constructor(
    initialTopLevelKey: NavKey,
    initialTopLevelStacks: LinkedHashMap<NavKey, SnapshotStateList<NavKey>>,
) {
    // Maintain a stack for each top level route
    private val topLevelStacks: LinkedHashMap<NavKey, SnapshotStateList<NavKey>> =
        initialTopLevelStacks

    // Expose the current top level route for consumers
    var topLevelKey: NavKey by mutableStateOf(initialTopLevelKey)
        private set

    // Expose the back stack so it can be rendered by the NavDisplay
    val backStack: SnapshotStateList<NavKey> = mutableStateListOf()

    init {
        updateBackStack()
    }

    constructor(startKey: NavKey) : this(
        startKey,
        linkedMapOf(startKey to mutableStateListOf(startKey)),
    )

    private fun updateBackStack() {
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }
    }

    fun addTopLevel(key: NavKey) {
        // If the top level doesn't exist, add it
        if (topLevelStacks[key] == null) {
            topLevelStacks[key] = mutableStateListOf(key)
        } else {
            // Otherwise just move it to the end of the stacks
            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: NavKey) {
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val currentStack = topLevelStacks[topLevelKey]

        if (currentStack != null && currentStack.size > 1) {
            currentStack.removeLast()
        } else if (topLevelStacks.size > 1) {
            topLevelStacks.remove(topLevelKey)
            topLevelKey = topLevelStacks.keys.last()
        }

        updateBackStack()
    }

    fun navigate(key: NavKey) {
        logcat { "navigate $key" }
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun goBack() {
        logcat { "goBack" }
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        // If the removed key was a top level key, remove the associated top level stack
        topLevelStacks.remove(removedKey)
        topLevelKey = topLevelStacks.keys.last()
        updateBackStack()
    }

    companion object {
        @Serializable
        private data class SaveableState(
            @Contextual val topLevelKey: NavKey,
            val topLevelStacks: Map<@Contextual NavKey, List<@Contextual NavKey>>,
        )

        fun Saver(serializersModule: SerializersModule): Saver<Navigator, String> = Saver(
            save = { backStack ->
                val json = Json {
                    this.classDiscriminator = "clazz_type"
                    this.serializersModule = SerializersModule {
                        this.include(serializersModule)
                    }
                    allowStructuredMapKeys = true
                }
                val state = SaveableState(
                    topLevelKey = backStack.topLevelKey,
                    topLevelStacks = backStack.topLevelStacks.mapValues { it.value.toList() },
                )
                json.encodeToString(SaveableState.serializer(), state)
            },
            restore = { savedString ->
                val json = Json {
                    this.classDiscriminator = "clazz_type"
                    this.serializersModule = SerializersModule {
                        this.include(serializersModule)
                    }
                    allowStructuredMapKeys = true
                }
                val state = json.decodeFromString(SaveableState.serializer(), savedString)

                @Suppress("SpreadOperator")
                val topLevelStacks =
                    state.topLevelStacks.mapValues { mutableStateListOf(*it.value.toTypedArray()) }
                Navigator(
                    initialTopLevelKey = state.topLevelKey,
                    initialTopLevelStacks = LinkedHashMap(topLevelStacks),
                )
            },
        )
    }
}

@Composable
fun rememberNavigator(startKey: NavKey, serializersModule: SerializersModule): Navigator =
    rememberSaveable(startKey, saver = Navigator.Saver(serializersModule)) {
        Navigator(startKey)
    }
