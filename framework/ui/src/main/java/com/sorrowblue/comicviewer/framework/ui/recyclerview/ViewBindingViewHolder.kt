package com.sorrowblue.comicviewer.framework.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewBindingViewHolder<out V : ViewBinding> private constructor(
    protected val binding: V
) : RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup, inflate: (LayoutInflater, ViewGroup, Boolean) -> V) :
            this(inflate.invoke(LayoutInflater.from(parent.context), parent, false))

    protected val context: Context get() = itemView.context
}
