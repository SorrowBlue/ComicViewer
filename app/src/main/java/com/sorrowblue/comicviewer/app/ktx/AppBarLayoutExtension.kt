package com.sorrowblue.comicviewer.app.ktx

import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.AppBarLayout

@BindingAdapter("isShown")
fun AppBarLayout.isShownForBinding(isShown: Boolean) {
    if (isShown) {
        children.firstOrNull()?.updateLayoutParams<AppBarLayout.LayoutParams> {
            scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        }
        setExpanded(true)
    } else {
        children.firstOrNull()?.updateLayoutParams<AppBarLayout.LayoutParams> {
            scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        }
        setExpanded(false)
    }
}
