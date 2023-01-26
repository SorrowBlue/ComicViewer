package com.sorrowblue.comicviewer.data.remote.reader.pdf

import android.content.Context
import com.sorrowblue.comicviewer.data.remote.reader.FileReader
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

internal class XpsFileReader @AssistedInject constructor(
    @ApplicationContext context: Context,
    @Assisted private val seekableInputStream: SeekableInputStream,
) : DocumentFileReader(context, "application/xps", seekableInputStream) {

    @AssistedFactory
    interface Factory : FileReader.Factory {
        override fun create(seekableInputStream: SeekableInputStream): XpsFileReader
    }
}
