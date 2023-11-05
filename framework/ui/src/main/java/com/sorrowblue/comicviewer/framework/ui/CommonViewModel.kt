package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

@Stable
class CommonViewModel : ViewModel() {

    var canScroll by mutableStateOf(false)
}
