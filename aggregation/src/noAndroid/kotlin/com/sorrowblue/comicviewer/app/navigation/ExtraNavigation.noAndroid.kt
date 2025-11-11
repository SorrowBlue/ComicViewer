package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State

context(graph: PlatformGraph, appNavigationState: Navigation3State)
internal actual fun EntryProviderScope<NavKey>.extraNavigation() {
}
