package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.SavedStateHandle

interface SaveableScreenState {
    val savedStateHandle: SavedStateHandle
}

@Composable
fun <R : SaveableScreenState> rememberSaveableScreenState(init: (SavedStateHandle) -> R): R {
    return rememberSaveable(
        saver = Saver(
            save = {
                it.savedStateHandle.savedStateProvider().saveState()
            },
            restore = {
                init(SavedStateHandle.createHandle(it, it))
            }
        )
    ) {
        init(SavedStateHandle())
    }
}
