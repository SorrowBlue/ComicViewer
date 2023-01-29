package com.sorrowblue.comicviewer.framework.ui.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

context (ViewModel)
fun <T> Flow<T>.mutableStateIn(init: T) = MutableStateFlow(init).also { mutable ->
    onEach { mutable.value = it }.launchIn(viewModelScope)
}
