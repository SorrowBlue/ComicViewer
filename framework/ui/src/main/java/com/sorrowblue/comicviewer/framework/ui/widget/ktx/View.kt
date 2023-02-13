package com.sorrowblue.comicviewer.framework.ui.widget.ktx

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("isVisible")
fun View.isVisibleForBinding(isVisible: Boolean) {
    this.isVisible = isVisible
}

@BindingAdapter("srcCompat", "error", requireAll = false)
fun ImageView.setSrcCompat(data: Any?, error: Drawable? = null) {
    if (data == null) return
    load(data) {
        error(error)
    }
}
