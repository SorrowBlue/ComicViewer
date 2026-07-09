/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Main view model
 */
class MainViewModel : ViewModel() {
    val shouldKeepSplash = MutableStateFlow(true)
    val isInitialized = MutableStateFlow(false)
}
