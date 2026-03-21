package com.sorrowblue.comicviewer.data.reader.document.startup

import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@GraphExtension(DocumentInitializerContext.ContextScope::class)
interface DocumentInitializerContext {
    val datastoreDataSource: DatastoreDataSource

    @ContributesTo(DataScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createReaderDocumentContext(): DocumentInitializerContext
    }

    @Scope
    annotation class ContextScope
}
