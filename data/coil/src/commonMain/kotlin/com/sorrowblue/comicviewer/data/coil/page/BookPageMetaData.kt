package com.sorrowblue.comicviewer.data.coil.page

import com.sorrowblue.comicviewer.data.coil.CoilMetaData
import kotlinx.io.Sink
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@OptIn(ExperimentalSerializationApi::class)
@Serializable
internal data class BookPageMetaData(
    val pageIndex: Int,
    val fileName: String = "",
    val fileSize: Long = 0,
) : CoilMetaData {

    override fun writeTo(sink: Sink) {
        sink.write(ProtoBuf.encodeToByteArray(this))
    }

    companion object : CoilMetaData.CompanionObject
}
