package com.sorrowblue.comicviewer.framework.ui.widget.ktx

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.viewpager2.widget.ViewPager2

@BindingAdapter("currentItem")
fun ViewPager2.setCurrentItemForBinding(value: Int) {
    this.currentItem = value
}

@InverseBindingAdapter(attribute = "currentItem")
fun ViewPager2.getCurrentItemForBinding(): Int = currentItem

@BindingAdapter("currentItemAttrChanged")
fun ViewPager2.bindOnPageChangeCallback(attrChange: InverseBindingListener) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            attrChange.onChange()
        }
    })
}
