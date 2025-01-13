package com.sorrowblue.comicviewer.multiplatform

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Main view model
 */
internal expect class MainViewModel : ViewModel {
    val shouldKeepSplash:MutableStateFlow<Boolean>
    val isInitialized: MutableStateFlow<Boolean>
}
