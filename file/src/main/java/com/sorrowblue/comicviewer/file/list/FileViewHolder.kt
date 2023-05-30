package com.sorrowblue.comicviewer.file.list

import android.view.View
import android.view.ViewTreeObserver


fun View.doOnGlobalLayout(action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action()
        }
    })
}
