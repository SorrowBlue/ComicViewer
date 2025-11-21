package com.sorrowblue.comicviewer.data.reader.document.di

import com.sorrowblue.comicviewer.data.reader.document.DocumentReaderDataSourceImpl
import com.sorrowblue.comicviewer.data.reader.document.startup.DocumentInitializer
import com.sorrowblue.comicviewer.domain.service.datasource.DocumentReaderDataSource
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(DataScope::class)
interface DocumentProviders {
    @Binds
    @IntoSet
    private val DocumentInitializer.bind: Initializer<*> get() = this

    @SingleIn(DataScope::class)
    @Provides
    private fun provideDocumentReaderDataSource(): DocumentReaderDataSource =
        DocumentReaderDataSourceImpl()
}
