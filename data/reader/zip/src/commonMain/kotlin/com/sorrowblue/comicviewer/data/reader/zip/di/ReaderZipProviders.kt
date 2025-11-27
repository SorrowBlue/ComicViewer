package com.sorrowblue.comicviewer.data.reader.zip.di

import com.sorrowblue.comicviewer.data.reader.zip.impl.ZipFileReader
import com.sorrowblue.comicviewer.data.storage.client.FileReaderFactory
import com.sorrowblue.comicviewer.data.storage.client.FileReaderKey
import com.sorrowblue.comicviewer.data.storage.client.FileReaderType
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap

@ContributesTo(DataScope::class)
interface ReaderZipProviders {
    @Binds
    @IntoMap
    @FileReaderKey(FileReaderType.Zip)
    private val ZipFileReader.Factory.bind: FileReaderFactory get() = this
}
