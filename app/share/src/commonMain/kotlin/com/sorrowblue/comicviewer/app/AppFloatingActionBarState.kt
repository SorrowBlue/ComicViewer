/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.FloatingActionContent
import com.sorrowblue.comicviewer.framework.ui.adaptive.FloatingActionButtonState
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalFloatingActionButtonState
import com.sorrowblue.comicviewer.framework.ui.adaptive.SupportingPaneRoleKey
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator
import kotlin.reflect.KClass
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * [AppFloatingActionBarState] を生成・記憶する Composable 関数。
 *
 * @param entryProvider [NavKey] に対応する [NavEntry] を提供する関数
 * @param navigator アプリのナビゲーションを管理する [Navigator]
 * @return [AppFloatingActionBarState]
 */
@Composable
context(context: PlatformContext)
internal fun rememberAppFloatingActionBarState(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    navigator: Navigator = LocalNavigator.current,
): AppFloatingActionBarState {
    val floatingActionContents = remember {
        context.appGraph<NavigationGraph>().floatingActionContents.toPersistentList()
    }
    val state =
        remember(floatingActionContents) { AppFloatingActionBarStateImpl(floatingActionContents) }
    val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    val isMultiPane = directive.maxHorizontalPartitions > 1
    state.visibleNavKeys = remember(navigator.backStack.toList(), isMultiPane) {
        val backStack = navigator.backStack
        val lastKey = backStack.lastOrNull() ?: return@remember persistentListOf()
        val lastEntry = entryProvider(lastKey)
        if ((isMultiPane && lastEntry.isSupportingOrExtraPane) || lastEntry.isDialog) {
            val planeKey = backStack.reversed().firstOrNull { key ->
                val entry = entryProvider(key)
                !entry.isSupportingOrExtraPane && !entry.isDialog
            }
            listOfNotNull(lastKey, planeKey)
        } else {
            listOf(lastKey)
        }.toPersistentList()
    }
    return state
}

/**
 * アプリのフローティングアクションバーの状態を保持・管理するインターフェース。
 */
@Stable
internal interface AppFloatingActionBarState {

    val floatingActionContents: PersistentList<FloatingActionContent>

    /** 現在画面上に表示されている [NavKey] のリスト。 */
    val visibleNavKeys: PersistentList<NavKey>

    /** [NavKey] の型に対応する [FloatingActionButtonState] のマップ。 */
    val fabStateList: SnapshotStateMap<KClass<out NavKey>, FloatingActionButtonState>

    /** [LocalFloatingActionButtonState] を提供するための [ProvidedValue]。 */
    val provides: ProvidedValue<SnapshotStateMap<KClass<out NavKey>, FloatingActionButtonState>>
        get() = LocalFloatingActionButtonState provides fabStateList

    /**
     * 指定した [navKey] が現在表示中の画面に含まれるかどうかを判定します。
     *
     * @param navKey 判定対象の [NavKey] クラス
     * @return 表示中の場合 true
     */
    fun isCurrent(navKey: KClass<out NavKey>): Boolean =
        visibleNavKeys.any { key -> key::class == navKey }
}

private class AppFloatingActionBarStateImpl(
    override val floatingActionContents: PersistentList<FloatingActionContent>,
) : AppFloatingActionBarState {
    override var visibleNavKeys: PersistentList<NavKey> by mutableStateOf(persistentListOf())
    override val fabStateList = mutableStateMapOf<KClass<out NavKey>, FloatingActionButtonState>()
}

private val ExtraMetadataClass =
    SupportingPaneSceneStrategy.extraPane().getValue(SupportingPaneRoleKey)::class
private val SupportingMetadataClass =
    SupportingPaneSceneStrategy.supportingPane().getValue(SupportingPaneRoleKey)::class
private val NavEntry<*>.isSupportingOrExtraPane: Boolean
    get() {
        val clazz = metadata[SupportingPaneRoleKey]?.let { it::class }
        return clazz == ExtraMetadataClass || clazz == SupportingMetadataClass
    }
private val NavEntry<*>.isDialog: Boolean
    get() = metadata.contains(DialogSceneStrategy.Companion.DialogKey)
