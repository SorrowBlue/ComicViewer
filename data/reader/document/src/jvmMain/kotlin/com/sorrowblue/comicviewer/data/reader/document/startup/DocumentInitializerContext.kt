package com.sorrowblue.comicviewer.data.reader.document.startup

import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderDataSource
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.withGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class DocumentInitializerScope

@GraphExtension(DocumentInitializerScope::class)
interface DocumentInitializerContext {
    val datastoreDataSource: DatastoreDataSource

    val pdfPluginDataSourceFactory: DocumentReaderDataSource

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createDocumentInitializerContext(): DocumentInitializerContext
    }
}

fun PlatformContext.withDocumentInitializerContext(block: DocumentInitializerContext.() -> Unit) {
    withGraph<DocumentInitializerContext.Factory, DocumentInitializerContext>(
        create = { createDocumentInitializerContext() },
    ) {
        block()
    }
}
