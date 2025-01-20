package com.sorrowblue.comicviewer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Main view model
 */
internal class MainViewModel : ViewModel() {
    val shouldKeepSplash = MutableStateFlow(true)
    val isInitialized = MutableStateFlow(false)
}
