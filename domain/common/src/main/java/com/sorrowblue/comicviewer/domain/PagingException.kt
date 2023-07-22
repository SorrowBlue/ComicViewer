package com.sorrowblue.comicviewer.domain

sealed class PagingException : RuntimeException() {

    data object NoNetwork : PagingException()
    data object InvalidAuth : PagingException()
    data object InvalidServer : PagingException()
    data object NotFound : PagingException()
}
