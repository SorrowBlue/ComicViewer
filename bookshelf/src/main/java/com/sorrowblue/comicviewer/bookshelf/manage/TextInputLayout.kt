package com.sorrowblue.comicviewer.bookshelf.manage

import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("textValidators")
fun TextInputLayout.setTextValidators(validators: List<TextValidator>) {
    editText?.doAfterTextChanged { editable ->
        val text = editable?.toString().orEmpty()
        var isError = false
        validators.forEach {
            if (!it.validate(text)) {
                it.isError.value = true
                if (!isError) {
                    error = it.error(context, text)
                    isError = true
                }
            } else {
                it.isError.value = false
            }
        }
        isErrorEnabled = isError
    }
}
