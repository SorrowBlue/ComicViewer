package com.sorrowblue.comicviewer.data.reader.document.di

import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource

interface ReaderDocumentContext {

    val datastoreDataSource: DatastoreDataSource
}
