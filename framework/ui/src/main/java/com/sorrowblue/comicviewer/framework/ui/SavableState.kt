package com.sorrowblue.comicviewer.framework.ui

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
    key: String? = null,
    action: (SavedStateHandle) -> R,
): R = rememberSaveable(
    inputs = inputs,
    saver = Saver(
        save = {
            it.savedStateHandle.savedStateProvider().saveState()
        },
        restore = {
            action(SavedStateHandle.createHandle(it, it))
        }
    ),
    key = key,
    init = { action(SavedStateHandle()) }
)
