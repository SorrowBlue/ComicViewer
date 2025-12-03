package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenRoot
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data class AuthenticationNavKey(val type: ScreenType) : ScreenKey

val AuthenticationKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(AuthenticationNavKey::class, AuthenticationNavKey.serializer())
    }
}

context(context: PlatformContext)
fun EntryProviderScope<NavKey>.authenticationEntryGroup(navigator: Navigator) {
    authenticationEntry(onBackClick = navigator::goBack, onComplete = navigator::goBack)
}

context(context: PlatformContext)
private fun EntryProviderScope<NavKey>.authenticationEntry(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<AuthenticationNavKey, AuthenticationScreenContext>(
        createContext = {
            context.require<AuthenticationScreenContext.Factory>()
                .createAuthenticationScreenContext()
        },
        metadata = NavDisplay.transitionMaterialFadeThrough(),
    ) {
        AuthenticationScreenRoot(
            screenType = it.type,
            onBackClick = onBackClick,
            onComplete = onComplete,
        )
    }
}
