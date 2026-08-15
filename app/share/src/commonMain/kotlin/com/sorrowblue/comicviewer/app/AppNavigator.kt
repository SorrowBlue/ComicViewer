/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfNavKey
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.rememberNavigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.serializer
import com.sorrowblue.comicviewer.framework.ui.navigation3.subclass
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic

@Composable
context(context: PlatformContext)
internal fun rememberAppNavigator(): Navigator {
    val graph = context.appGraph<NavigationGraph>()
    val topLevelRoutes = retain {
        graph.navigationKeys.sortedBy { it.order }.toSet()
    }
    val configuration = retain {
        SavedStateConfiguration {
            serializersModule = SerializersModule {
                contextual(SnapshotStateListSerializer(PolymorphicSerializer(NavKey::class)))
                polymorphic(NavKey::class) {
                    graph.navKeySubclassMap.forEach {
                        subclass(it.subclass, it.serializer)
                    }
                }
            }
        }
    }
    return rememberNavigator(
        startKey = BookshelfNavKey,
        topLevelRoutes = topLevelRoutes,
        configuration = configuration,
    )
}
