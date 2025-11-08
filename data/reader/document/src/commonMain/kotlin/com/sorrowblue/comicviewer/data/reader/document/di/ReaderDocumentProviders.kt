package com.sorrowblue.comicviewer.data.reader.document.di

import com.sorrowblue.comicviewer.data.reader.document.DocumentFileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap

@ContributesTo(DataScope::class)
interface ReaderDocumentProviders {
    @Binds
    @IntoMap
    @FileReaderKey(FileReaderType.Document)
    private fun DocumentFileReader.Factory.bind(): FileReaderFactory = this
}
