package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import org.koin.core.annotation.Single

/**
 * Main view model
 */
@KoinViewModel
internal class MainViewModel : ViewModel() {
    val shouldKeepSplash = MutableStateFlow(true)
    val isInitialized = MutableStateFlow(false)
}
