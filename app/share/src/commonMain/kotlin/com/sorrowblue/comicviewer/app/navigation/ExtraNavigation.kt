package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

context(context: PlatformContext)
internal expect fun EntryProviderScope<NavKey>.extraNavigation(navigator: Navigator)
