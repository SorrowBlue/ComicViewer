package com.sorrowblue.comicviewer.book

enum class LoadingState {
    LOADING,
    ERROR;

    fun isError() = this == ERROR
}
