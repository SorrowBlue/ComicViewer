package com.sorrowblue.comicviewer.framework.ui.widget.ktx

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("isVisible")
fun View.isVisibleForBinding(isVisible: Boolean) {
    this.isVisible = isVisible
}

@BindingAdapter("srcCompat", "memoryCacheEnabled", requireAll = false)
fun ImageView.setSrcCompat(data: Any?, memoryCacheEnabled: Boolean = false) {
    if (data == null) return
    load(data) {
        if (memoryCacheEnabled) {
            memoryCacheKey(data.hashCode().toString())
        }
    }
}
