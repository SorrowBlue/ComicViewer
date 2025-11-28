package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenRoot
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
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

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.tutorialEntryGroup(navigator: Navigator) {
    tutorialEntry(onComplete = navigator::goBack)
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.tutorialEntry(onComplete: () -> Unit) {
    entryScreen<TutorialKey, TutorialScreenContext>(
        createContext = {
            context.require<TutorialScreenContext.Factory>().createTutorialScreenContext()
        },
    ) {
        TutorialScreenRoot(onComplete = onComplete)
    }
}
