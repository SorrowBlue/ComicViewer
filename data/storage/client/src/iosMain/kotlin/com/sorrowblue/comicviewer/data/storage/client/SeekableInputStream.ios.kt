package com.sorrowblue.comicviewer.data.storage.client

actual interface SeekableInputStream : AutoCloseable {

    val path: String
}
