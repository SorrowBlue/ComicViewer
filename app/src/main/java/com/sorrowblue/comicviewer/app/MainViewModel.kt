package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class MainViewModel : ViewModel() {
    val shouldKeepSplash = MutableStateFlow(true)
    val isInitialized = MutableStateFlow(false)
}
