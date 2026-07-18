/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class LicenseViewModel(private val licenseeHelper: LicenseeHelper) : ViewModel() {

    suspend fun loadLibraries(): String = licenseeHelper.loadLibraries().decodeToString()
}
