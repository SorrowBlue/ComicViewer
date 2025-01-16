package com.sorrowblue.comicviewer

import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Main view model
 */
@KoinViewModel
internal actual class MainViewModel() : ViewModel() {
    actual val shouldKeepSplash = MutableStateFlow(true)
    actual val isInitialized = MutableStateFlow(false)
}
