package com.sorrowblue.comicviewer.book

enum class LoadingState {
    LOADING,
    NOT_FOUND,
    SUCCESS,
    ERROR;

    fun isError() = this == ERROR
}
