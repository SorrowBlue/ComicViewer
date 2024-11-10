package com.sorrowblue.comicviewer.domain.model

sealed class PagingException : RuntimeException() {

    class NoNetwork : PagingException()
    class InvalidAuth : PagingException()
    class InvalidServer : PagingException()
    class NotFound : PagingException()
}
