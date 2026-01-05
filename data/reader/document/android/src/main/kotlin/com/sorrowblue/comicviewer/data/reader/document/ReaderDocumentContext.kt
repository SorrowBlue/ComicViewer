package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ReaderDocumentContextScope

@GraphExtension(ReaderDocumentContextScope::class)
interface ReaderDocumentContext {
    val datastoreDataSource: DatastoreDataSource

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createReaderDocumentContext(): ReaderDocumentContext
    }
}
