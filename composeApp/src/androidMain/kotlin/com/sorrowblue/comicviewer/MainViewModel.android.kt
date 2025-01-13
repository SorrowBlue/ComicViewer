package com.sorrowblue.comicviewer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Main view model
 */
@HiltViewModel
internal actual class MainViewModel @Inject constructor() : ViewModel() {
    actual val shouldKeepSplash = MutableStateFlow(true)
    actual val isInitialized = MutableStateFlow(false)
}
