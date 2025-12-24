package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained

context(factory: AuthenticationScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.authenticationNavEntry(navigator: Navigator) {
    entry<AuthenticationNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
        with(rememberRetained { factory.createAuthenticationScreenContext() }) {
            AuthenticationScreenRoot(
                screenType = it.type,
                onBackClick = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
}
