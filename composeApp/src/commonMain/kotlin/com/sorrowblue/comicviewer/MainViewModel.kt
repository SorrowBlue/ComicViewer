package com.sorrowblue.comicviewer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel

/**
 * Main view model
 */
@KoinViewModel
internal class MainViewModel : ViewModel() {
    val shouldKeepSplash = MutableStateFlow(true)
    val isInitialized = MutableStateFlow(false)
}
