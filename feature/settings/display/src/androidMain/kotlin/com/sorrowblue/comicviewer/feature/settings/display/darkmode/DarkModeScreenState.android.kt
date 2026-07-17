/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.display.darkmode

import androidx.appcompat.app.AppCompatDelegate
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode

internal actual fun updateDarkMode(darkMode: DarkMode) {
    when (darkMode) {
        DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    }.let(AppCompatDelegate::setDefaultNightMode)
}
