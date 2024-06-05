package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

internal class MainViewModel : ViewModel() {
    var shouldKeepSplash = MutableStateFlow(true)
    var isInitialized = MutableStateFlow(false)
}
