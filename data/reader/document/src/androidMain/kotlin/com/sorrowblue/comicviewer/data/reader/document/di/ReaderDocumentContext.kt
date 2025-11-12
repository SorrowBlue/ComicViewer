package com.sorrowblue.comicviewer.data.reader.document.di

import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@ContributesTo(AppScope::class)
@GraphExtension
interface ReaderDocumentContext {
    val datastoreDataSource: DatastoreDataSource
}
