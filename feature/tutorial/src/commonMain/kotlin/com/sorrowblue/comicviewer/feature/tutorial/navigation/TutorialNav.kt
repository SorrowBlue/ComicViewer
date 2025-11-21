package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val TutorialKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(TutorialKey::class, TutorialKey.serializer())
    }
}

@Serializable
data object TutorialKey : ScreenKey

context(graph: PlatformGraph)
fun EntryProviderScope<NavKey>.tutorialEntryGroup(navigator: Navigator) {
    tutorialEntry(onComplete = navigator::goBack)
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.tutorialEntry(onComplete: () -> Unit) {
    entryScreen<TutorialKey, TutorialScreenContext>(
        createContext = { (graph as TutorialScreenContext.Factory).createTutorialScreenContext() },
    ) {
        TutorialScreenRoot(onComplete = onComplete)
    }
}
