package com.sorrowblue.comicviewer.domain

sealed class PagingException() : RuntimeException() {

    object NoNetwork: PagingException()
    object InvalidAuth : PagingException()
    object InvalidServer : PagingException()
    object NotFound : PagingException()
}
