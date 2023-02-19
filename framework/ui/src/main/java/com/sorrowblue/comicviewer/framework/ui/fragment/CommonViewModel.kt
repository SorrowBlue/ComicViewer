package com.sorrowblue.comicviewer.framework.ui.fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {

    val snackbarMessage = MutableSharedFlow<String>(0, 1, BufferOverflow.SUSPEND)
    var shouldKeepOnScreen = true

    val isRestored = MutableSharedFlow<Boolean>(1)
}
