package com.sorrowblue.comicviewer.framework.ui.widget.ktx

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.slider.Slider

@BindingAdapter("value")
fun Slider.setValueForBinding(value: Int) {
    this.value = value.toFloat()
}

@InverseBindingAdapter(attribute = "value")
fun Slider.getValueForBinding(): Int = value.toInt()

@BindingAdapter("valueAttrChanged")
fun Slider.setOnChangeValueListener(attrChange: InverseBindingListener) {
    addOnChangeListener { _, _, fromUser ->
        if (fromUser) attrChange.onChange()
    }
}
