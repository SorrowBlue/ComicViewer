package com.sorrowblue.comicviewer.bookshelf.manage

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

abstract class TextValidator {

    val isError = MutableStateFlow(false)

    abstract fun validate(value: String): Boolean

    abstract fun error(context: Context, value: String): String
}

fun List<TextValidator>.isErrorFlow(): Flow<Boolean> {
    return combine(map { it.isError }) { booleans ->
        booleans.any { it }
    }
}
