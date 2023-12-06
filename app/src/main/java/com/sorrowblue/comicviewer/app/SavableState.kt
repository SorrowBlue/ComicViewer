package com.sorrowblue.comicviewer.app

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.SavedStateHandle

interface SavableState {
    val savedStateHandle: SavedStateHandle
}

@SuppressLint("RestrictedApi")
@Composable
fun <R : SavableState> rememberSavableState(
    vararg inputs: Any?,
    restore: (value: SavedStateHandle) -> R?,
    key: String? = null,
    init: () -> R,
): R = rememberSaveable(
    inputs = inputs,
    saver = Saver(
        save = {
            it.savedStateHandle.savedStateProvider().saveState()
        },
        restore = {
            restore(SavedStateHandle.createHandle(it, it))
        }
    ),
    key = key,
    init = init
)
