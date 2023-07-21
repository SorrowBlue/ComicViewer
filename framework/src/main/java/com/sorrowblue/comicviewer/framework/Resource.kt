package com.sorrowblue.comicviewer.framework

sealed interface Resource<out D, out E> {
    class Success<D>(val data: D) : Resource<D, Nothing>

    class Error<E>(val error: E) : Resource<Nothing, E>
}
