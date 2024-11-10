@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldAdaptStrategies
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.DefaultThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.util.fastMap
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
val DefaultSupportingPaneHistory: List<ThreePaneScaffoldDestinationItem<Nothing>> =
    listOf(ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main))

@ExperimentalMaterial3AdaptiveApi
@Composable
inline fun <reified T : @Serializable Any> rememberCanonicalScaffoldNavigator(
    scaffoldDirective: PaneScaffoldDirective =
        calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()),
    adaptStrategies: ThreePaneScaffoldAdaptStrategies =
        SupportingPaneScaffoldDefaults.adaptStrategies(),
    isDestinationHistoryAware: Boolean = true,
    initialDestinationHistory: List<ThreePaneScaffoldDestinationItem<T>> = DefaultSupportingPaneHistory,
): ThreePaneScaffoldNavigator<T> = rememberThreePaneScaffoldNavigator(
    kSerializerSaver<T>(),
    scaffoldDirective,
    adaptStrategies,
    isDestinationHistoryAware,
    initialDestinationHistory
)

@ExperimentalMaterial3AdaptiveApi
@Composable
fun <T> rememberThreePaneScaffoldNavigator(
    serializableSaver: SerializableSaver<T>,
    scaffoldDirective: PaneScaffoldDirective,
    adaptStrategies: ThreePaneScaffoldAdaptStrategies,
    isDestinationHistoryAware: Boolean,
    initialDestinationHistory: List<ThreePaneScaffoldDestinationItem<T>>,
): ThreePaneScaffoldNavigator<T> =
    rememberSaveable(
        saver = saver(
            serializableSaver,
            scaffoldDirective,
            adaptStrategies,
            isDestinationHistoryAware
        )
    ) {
        DefaultThreePaneScaffoldNavigator(
            initialDestinationHistory = initialDestinationHistory,
            initialScaffoldDirective = scaffoldDirective,
            initialAdaptStrategies = adaptStrategies,
            initialIsDestinationHistoryAware = isDestinationHistoryAware
        )
    }.apply {
        this.scaffoldDirective = scaffoldDirective
        this.adaptStrategies = adaptStrategies
        this.isDestinationHistoryAware = isDestinationHistoryAware
    }

private fun <T> saver(
    serializableSaver: SerializableSaver<T>,
    initialScaffoldDirective: PaneScaffoldDirective,
    initialAdaptStrategies: ThreePaneScaffoldAdaptStrategies,
    initialDestinationHistoryAware: Boolean,
): Saver<DefaultThreePaneScaffoldNavigator<T>, *> {
    val destinationItemSaver = destinationItemSaver(serializableSaver)
    return listSaver(
        save = {
            DefaultThreePaneScaffoldNavigator::class.java.getDeclaredField("destinationHistory")
                .let { field ->
                    field.isAccessible = true
                    @Suppress("UNCHECKED_CAST")
                    field.get(it) as SnapshotStateList<ThreePaneScaffoldDestinationItem<T>>
                }.fastMap { destination ->
                    with(destinationItemSaver) { save(destination) }
                }
        },
        restore = {
            DefaultThreePaneScaffoldNavigator(
                initialDestinationHistory =
                it.fastMap { savedDestination ->
                    destinationItemSaver.restore(savedDestination!!)!!
                },
                initialScaffoldDirective = initialScaffoldDirective,
                initialAdaptStrategies = initialAdaptStrategies,
                initialIsDestinationHistoryAware = initialDestinationHistoryAware
            )
        }
    )
}

inline fun <reified Original : @Serializable Any> kSerializerSaver() =
    object : SerializableSaver<Original> {
        override fun save(value: Original): String {
            return Json.Default.encodeToString(value)
        }

        override fun restore(value: String): Original {
            return Json.decodeFromString(value)
        }
    }

interface SerializableSaver<Original> {
    fun save(value: Original): String

    fun restore(value: String): Original?
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun <T> destinationItemSaver(
    serializableSaver: SerializableSaver<T>,
): Saver<ThreePaneScaffoldDestinationItem<T>, Any> =
    listSaver(
        save = {
            listOf(it.pane, it.contentKey?.let(serializableSaver::save))
        },
        restore = {
            (
                ThreePaneScaffoldDestinationItem(
                    pane = it[0] as ThreePaneScaffoldRole,
                    contentKey = (it[1] as? String)?.let(serializableSaver::restore)
                )
                )
        }
    )
