package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenContext
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.receiveBookEntry(onCloseClick: () -> Unit) {
    entryScreen<ReceiveBookKey, ReceiveBookScreenContext>(
        createContext = {
            context.require<ReceiveBookScreenContext.Factory>().createReceiveBookScreenContext()
        },
        metadata = NavDisplay.transitionMaterialSharedAxisZ(),
    ) {
        ReceiveBookScreenRoot(it.uri, onCloseClick)
    }
}
