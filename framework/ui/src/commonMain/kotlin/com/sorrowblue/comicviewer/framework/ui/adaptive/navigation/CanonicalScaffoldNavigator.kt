package com.sorrowblue.comicviewer.framework.ui.adaptive.navigation

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.MutableThreePaneScaffoldState
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldAdaptStrategies
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculateThreePaneScaffoldValue
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastMap
import com.sorrowblue.comicviewer.framework.ui.kSerializableByteArraySaver
import kotlinx.serialization.Serializable

@Composable
inline fun <reified T : @Serializable Any> rememberCanonicalScaffoldNavigator(
    scaffoldDirective: PaneScaffoldDirective =
        calculatePaneScaffoldDirective(currentWindowAdaptiveInfo()),
    adaptStrategies: ThreePaneScaffoldAdaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(),
    isDestinationHistoryAware: Boolean = true,
    initialDestinationHistory: List<ThreePaneScaffoldDestinationItem<T>> = DefaultSupportingPaneHistory,
): ThreePaneScaffoldNavigator<T> = rememberCanonicalScaffoldNavigator(
    kSerializableByteArraySaver<T>(),
    scaffoldDirective,
    adaptStrategies,
    isDestinationHistoryAware,
    initialDestinationHistory
)

/**
 * @see
 *    androidx.compose.material3.adaptive.navigation.rememberThreePaneScaffoldNavigator
 */
@PublishedApi
@Composable
internal fun <T> rememberCanonicalScaffoldNavigator(
    kSerializableSaver: Saver<T, ByteArray>,
    scaffoldDirective: PaneScaffoldDirective,
    adaptStrategies: ThreePaneScaffoldAdaptStrategies,
    isDestinationHistoryAware: Boolean,
    initialDestinationHistory: List<ThreePaneScaffoldDestinationItem<T>>,
): ThreePaneScaffoldNavigator<T> =
    rememberSaveable(
        saver = DefaultCanonicalScaffoldNavigator.saver(
            kSerializableSaver,
            scaffoldDirective,
            adaptStrategies,
            isDestinationHistoryAware
        )
    ) {
        DefaultCanonicalScaffoldNavigator(
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

/**
 * @see [androidx.compose.material3.adaptive.navigation.DefaultThreePaneScaffoldNavigator]
 */
private class DefaultCanonicalScaffoldNavigator<T>(
    initialDestinationHistory: List<ThreePaneScaffoldDestinationItem<T>>,
    initialScaffoldDirective: PaneScaffoldDirective,
    initialAdaptStrategies: ThreePaneScaffoldAdaptStrategies,
    initialIsDestinationHistoryAware: Boolean,
) : ThreePaneScaffoldNavigator<T> {

    val destinationHistory =
        mutableStateListOf<ThreePaneScaffoldDestinationItem<T>>().apply {
            addAll(initialDestinationHistory)
        }

    override var scaffoldDirective by mutableStateOf(initialScaffoldDirective)

    override var isDestinationHistoryAware by mutableStateOf(initialIsDestinationHistoryAware)

    var adaptStrategies by mutableStateOf(initialAdaptStrategies)

    override val currentDestination
        get() = destinationHistory.lastOrNull()

    override val scaffoldValue by derivedStateOf {
        calculateScaffoldValue(destinationHistory.lastIndex)
    }

    // Must be updated whenever `destinationHistory` changes to keep in sync.
    override val scaffoldState = MutableThreePaneScaffoldState(scaffoldValue)

    override fun peekPreviousScaffoldValue(
        backNavigationBehavior: BackNavigationBehavior,
    ): ThreePaneScaffoldValue {
        val index = getPreviousDestinationIndex(backNavigationBehavior)
        return if (index == -1) scaffoldValue else calculateScaffoldValue(index)
    }

    override suspend fun navigateTo(pane: ThreePaneScaffoldRole, contentKey: T?) {
        destinationHistory.add(ThreePaneScaffoldDestinationItem(pane, contentKey))
        animateStateToCurrentScaffoldValue()
    }

    override fun canNavigateBack(backNavigationBehavior: BackNavigationBehavior): Boolean =
        getPreviousDestinationIndex(backNavigationBehavior) >= 0

    override suspend fun navigateBack(
        backNavigationBehavior: BackNavigationBehavior,
    ): Boolean {
        val previousDestinationIndex = getPreviousDestinationIndex(backNavigationBehavior)
        if (previousDestinationIndex < 0) {
            destinationHistory.clear()
            animateStateToCurrentScaffoldValue()
            return false
        }
        val targetSize = previousDestinationIndex + 1
        while (destinationHistory.size > targetSize) {
            destinationHistory.removeLast()
        }
        animateStateToCurrentScaffoldValue()
        return true
    }

    override suspend fun seekBack(backNavigationBehavior: BackNavigationBehavior, fraction: Float) {
        if (fraction == 0f) {
            animateStateToCurrentScaffoldValue()
        } else {
            val previousScaffoldValue = peekPreviousScaffoldValue(backNavigationBehavior)
            scaffoldState.seekTo(fraction, previousScaffoldValue, isPredictiveBackInProgress = true)
        }
    }

    private suspend fun animateStateToCurrentScaffoldValue() {
        scaffoldState.animateTo(scaffoldValue)
    }

    private fun getPreviousDestinationIndex(backNavBehavior: BackNavigationBehavior): Int {
        if (destinationHistory.size <= 1) {
            // No previous destination
            return -1
        }
        when (backNavBehavior) {
            BackNavigationBehavior.PopLatest -> return destinationHistory.lastIndex - 1
            BackNavigationBehavior.PopUntilScaffoldValueChange ->
                for (previousDestinationIndex in destinationHistory.lastIndex - 1 downTo 0) {
                    val previousValue = calculateScaffoldValue(previousDestinationIndex)
                    if (previousValue != scaffoldValue) {
                        return previousDestinationIndex
                    }
                }

            BackNavigationBehavior.PopUntilCurrentDestinationChange ->
                for (previousDestinationIndex in destinationHistory.lastIndex - 1 downTo 0) {
                    val destination = destinationHistory[previousDestinationIndex].pane
                    if (destination != currentDestination?.pane) {
                        return previousDestinationIndex
                    }
                }

            BackNavigationBehavior.PopUntilContentChange ->
                for (previousDestinationIndex in destinationHistory.lastIndex - 1 downTo 0) {
                    val contentKey = destinationHistory[previousDestinationIndex].contentKey
                    if (contentKey != currentDestination?.contentKey) {
                        return previousDestinationIndex
                    }
                    // A scaffold value change also counts as a content change.
                    val previousValue = calculateScaffoldValue(previousDestinationIndex)
                    if (previousValue != scaffoldValue) {
                        return previousDestinationIndex
                    }
                }
        }

        return -1
    }

    private fun calculateScaffoldValue(destinationIndex: Int) =
        if (destinationIndex == -1) {
            calculateThreePaneScaffoldValue(
                scaffoldDirective.maxHorizontalPartitions,
                adaptStrategies,
                null
            )
        } else if (isDestinationHistoryAware) {
            calculateThreePaneScaffoldValue(
                scaffoldDirective.maxHorizontalPartitions,
                adaptStrategies,
                destinationHistory.subList(0, destinationIndex + 1)
            )
        } else {
            calculateThreePaneScaffoldValue(
                scaffoldDirective.maxHorizontalPartitions,
                adaptStrategies,
                destinationHistory[destinationIndex]
            )
        }

    companion object {

        /** To keep destination history saved */
        fun <T> saver(
            byteArraySaver: Saver<T, ByteArray>,
            initialScaffoldDirective: PaneScaffoldDirective,
            initialAdaptStrategies: ThreePaneScaffoldAdaptStrategies,
            initialDestinationHistoryAware: Boolean,
        ) = listSaver<DefaultCanonicalScaffoldNavigator<T>, List<Any?>>(
            save = {
                it.destinationHistory.fastMap { destination ->
                    listOf(
                        destination.pane,
                        destination.contentKey?.let { contentKey ->
                            with(byteArraySaver) { save(contentKey) }
                        }
                    )
                }
            },
            restore = {
                DefaultCanonicalScaffoldNavigator(
                    initialDestinationHistory = it.fastMap { savedDestination ->
                        ThreePaneScaffoldDestinationItem(
                            savedDestination[0] as ThreePaneScaffoldRole,
                            (savedDestination[1] as? ByteArray)?.let(byteArraySaver::restore)
                        )
                    },
                    initialScaffoldDirective = initialScaffoldDirective,
                    initialAdaptStrategies = initialAdaptStrategies,
                    initialIsDestinationHistoryAware = initialDestinationHistoryAware
                )
            }
        )
    }
}

/**
 * @see [androidx.compose.material3.adaptive.navigation.DefaultSupportingPaneHistory]
 */
@PublishedApi
internal val DefaultSupportingPaneHistory: List<ThreePaneScaffoldDestinationItem<Nothing>> =
    listOf(ThreePaneScaffoldDestinationItem(SupportingPaneScaffoldRole.Main))
