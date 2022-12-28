package com.sorrowblue.comicviewer.framework.ui.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import java.io.Serializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

interface SupportSafeArgs {
    val savedStateHandle: SavedStateHandle
}

inline fun <reified Args : NavArgs> SupportSafeArgs.navArgs() = NavArgsLazy(Args::class) {
    val bundle = Bundle()
    savedStateHandle.keys().forEach {
        val value = savedStateHandle.get<Any>(it)
        if (value is Serializable) {
            bundle.putSerializable(it, value)
        } else if (value is Parcelable) {
            bundle.putParcelable(it, value)
        }
    }
    bundle
}

context(ViewModel)
fun <T> Flow<T>.stateIn(started: SharingStarted = SharingStarted.Eagerly, init: () -> T) =
    stateIn(viewModelScope, started = started, init.invoke())
