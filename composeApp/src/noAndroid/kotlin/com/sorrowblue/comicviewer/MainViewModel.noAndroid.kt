package com.sorrowblue.comicviewer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Main view model
 */
internal actual class MainViewModel : ViewModel() {
    actual val shouldKeepSplash = MutableStateFlow(true)
    actual val isInitialized = MutableStateFlow(false)
}
