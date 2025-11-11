package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenContext
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen

context(graph: PlatformGraph)
fun EntryProviderScope<NavKey>.receiveBookEntry(onCloseClick: () -> Unit) {
    entryScreen<ReceiveBookKey, ReceiveBookScreenContext>(
        createContext = {
            (graph as ReceiveBookScreenContext.Factory).createReceiveBookScreenContext()
        },
    ) {
        ReceiveBookScreenRoot(it.uri)
    }
}
