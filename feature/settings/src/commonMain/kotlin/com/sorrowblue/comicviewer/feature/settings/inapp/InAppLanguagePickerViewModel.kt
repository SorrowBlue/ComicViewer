/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.inapp

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.framework.designsystem.locale.AppLocaleIso
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class InAppLanguagePickerViewModel(val appLocaleIso: AppLocaleIso) : ViewModel()
