/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenRoot
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntry
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationNavKey(val type: ScreenType) : NavKey

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun authenticationNavEntry(navigator: Navigator) {
    scope.entry<AuthenticationNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
        AuthenticationScreenRoot(
            screenType = it.type,
            onBackClick = navigator::goBack,
            onComplete = navigator::goBack,
        )
    }
}
