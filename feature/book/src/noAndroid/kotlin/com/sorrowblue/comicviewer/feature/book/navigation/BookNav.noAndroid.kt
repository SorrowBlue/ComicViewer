package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.common.PlatformGraph

context(graph: PlatformGraph)
internal actual fun EntryProviderScope<NavKey>.receiveBookEntry(onCloseClick: () -> Unit) {
    // Do nothing
}
