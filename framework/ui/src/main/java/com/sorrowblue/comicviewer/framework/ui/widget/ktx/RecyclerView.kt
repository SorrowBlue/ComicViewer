package com.sorrowblue.comicviewer.framework.ui.widget.ktx

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("spanCount")
fun RecyclerView.setSpanCount(spanCount: Int) {
    (layoutManager as? GridLayoutManager)?.spanCount = spanCount
}
