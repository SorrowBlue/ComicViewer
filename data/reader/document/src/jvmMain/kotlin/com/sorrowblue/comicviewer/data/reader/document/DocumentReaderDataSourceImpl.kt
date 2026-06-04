package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class DocumentReaderDataSourceImpl : DocumentReaderDataSource {

    override val version: String get() = ""

    override fun initializePdfPlugin(rootPath: String): DocumentReaderState {
        return DocumentReaderState.Success
    }
}
