package com.sorrowblue.comicviewer.framework.ui.widget.ktx

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.button.MaterialButtonToggleGroup

@BindingAdapter("checkedButton")
fun MaterialButtonToggleGroup.setCheckedButtonForBinding(id: Int) {
    check(id)
}

@InverseBindingAdapter(attribute = "checkedButton")
fun MaterialButtonToggleGroup.getCheckedButtonForBinding() = checkedButtonId

@BindingAdapter("checkedButtonAttrChanged")
fun MaterialButtonToggleGroup.setOnButtonCheckedInverseBindingListener(attrChange: InverseBindingListener) {
    addOnButtonCheckedListener { _, _, isChecked -> if (isChecked) attrChange.onChange() }
}
