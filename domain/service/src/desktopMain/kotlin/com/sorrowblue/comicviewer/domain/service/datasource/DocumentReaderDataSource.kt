package com.sorrowblue.comicviewer.domain.service.datasource

interface DocumentReaderDataSource {
    val version: String

    fun initializePdfPlugin(rootPath: String): DocumentReaderState
}

sealed interface DocumentReaderState {
    data class InvalidVersion(val version: String) : DocumentReaderState

    data object Success : DocumentReaderState

    data object Error : DocumentReaderState

    data object NotFound : DocumentReaderState
}
