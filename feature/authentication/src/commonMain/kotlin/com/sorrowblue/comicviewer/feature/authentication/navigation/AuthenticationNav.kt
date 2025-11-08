package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenRoot
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.AppNavigationState
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.entryScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data class AuthenticationKey(val type: ScreenType) : ScreenKey

val AuthenticationKeySerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(AuthenticationKey::class, AuthenticationKey.serializer())
    }
}

context(graph: PlatformGraph, state: AppNavigationState)
fun EntryProviderScope<NavKey>.authenticationEntryGroup() {
    authenticationEntry(onBackClick = state::onBackPressed, onComplete = state::onBackPressed)
}

context(graph: PlatformGraph)
private fun EntryProviderScope<NavKey>.authenticationEntry(
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    entryScreen<AuthenticationKey, AuthenticationScreenContext>(
        createContext = {
            (graph as AuthenticationScreenContext.Factory)
                .createAuthenticationScreenContext()
        },
    ) {
        AuthenticationScreenRoot(
            screenType = it.type,
            onBackClick = onBackClick,
            onComplete = onComplete,
        )
    }
}
