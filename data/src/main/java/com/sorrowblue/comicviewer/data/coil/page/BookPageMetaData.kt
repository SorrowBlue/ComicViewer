package com.sorrowblue.comicviewer.data.coil.page

import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
internal data class BookPageMetaData(
    val pageIndex: Int,
    val fileName: String = "",
    val fileSize: Long = 0
) {

    @OptIn(ExperimentalSerializationApi::class)
    fun write(output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(this))
    }
}
