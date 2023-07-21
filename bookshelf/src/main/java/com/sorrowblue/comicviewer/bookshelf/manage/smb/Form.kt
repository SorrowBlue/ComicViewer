package com.sorrowblue.comicviewer.bookshelf.manage.smb

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class Form(
    init: String? = null,
    val singleLine: Boolean = false,
    private val validate: (String) -> Boolean
) {
    private var skipValidate = MutableStateFlow(init == null)
    private val _value = MutableStateFlow(init.orEmpty())
    val flow = _value.asStateFlow()
    val isError = _value.combine(skipValidate) { v, s -> if (s) false else validate.invoke(v) }
    fun edit(value: String) {
        skipValidate.value = false
        _value.value = value.filter { it != '\n' }
    }

    fun validate() {
        skipValidate.value = false
    }
}

class Form2<T : Any?>(init: T) {

    private val _value = MutableStateFlow(init)

    val flow = _value.asStateFlow()

    fun edit(value: T) {
        _value.value = value
    }
}
