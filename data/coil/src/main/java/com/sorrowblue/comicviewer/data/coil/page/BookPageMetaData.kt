package com.sorrowblue.comicviewer.data.coil.page

import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class BookPageMetaData(
    val pageIndex: Int,
    val fileName: String = "",
    val fileSize: Long = 0,
) {

    companion object {
        fun from(source: BufferedSource) =
            ProtoBuf.decodeFromByteArray<BookPageMetaData>(source.readByteArray())
    }

    fun write(output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(this))
    }
}
