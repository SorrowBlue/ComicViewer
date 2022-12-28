package com.sorrowblue.comicviewer.server.management.util

import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("textValidator")
fun TextInputLayout.setTextValidator(validator: TextValidator) {
    editText?.doAfterTextChanged {
        if (!validator.validate(it?.toString().orEmpty())) {
            error = validator.error(context, it?.toString().orEmpty())
        } else {
            isErrorEnabled = false
        }
        validator.isError.value = isErrorEnabled
    }
}

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
