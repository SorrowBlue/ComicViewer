package com.sorrowblue.comicviewer.data.coil.meta

import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream

@Serializable
internal data class BookPageMetaData(
    val pageIndex: Int,
    val fileName: String = "",
    val fileSize: Long = 0
) {

    @OptIn(ExperimentalSerializationApi::class)
    fun write(output: OutputStream) {
        Json.encodeToStream(serializer(), this, output)
    }
}
