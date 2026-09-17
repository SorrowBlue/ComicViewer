/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPlaceholder
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DarkModeNavKey
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
internal class DisplaySettingsDetailPlaceholder : SettingsDetailPlaceholder {

    @Composable
    override fun invoke(navigator: Navigator) {
        DisplaySettingsScreenRoot(
            onBackClick = dropUnlessResumed { navigator.goBack() },
            onDarkModeClick = dropUnlessResumed { navigator.navigate(DarkModeNavKey) },
        )
    }
}
