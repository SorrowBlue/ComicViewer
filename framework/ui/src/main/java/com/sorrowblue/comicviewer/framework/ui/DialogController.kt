package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

open class DialogController<T>(value: T) {

    var isShow: Boolean by mutableStateOf(false)
        protected set
    var value by mutableStateOf(value)
        protected set

    fun show(value: T) {
        this.value = value
        isShow = true
    }

    fun dismiss() {
        isShow = false
    }
}
