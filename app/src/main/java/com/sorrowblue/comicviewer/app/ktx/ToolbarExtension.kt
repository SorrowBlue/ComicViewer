package com.sorrowblue.comicviewer.app.ktx

import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter

@BindingAdapter("menu")
fun Toolbar.setMenuForBinding(@MenuRes resId: Int) {
    if (resId == View.NO_ID) {
        menu.clear()
    } else {
        inflateMenu(resId)
    }
}
