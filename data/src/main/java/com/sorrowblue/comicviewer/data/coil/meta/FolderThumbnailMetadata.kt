package com.sorrowblue.comicviewer.data.coil.meta

import java.io.OutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSource

@Serializable
internal class FolderThumbnailMetadata(var thumbnail: List<String> = emptyList()) {

    @OptIn(ExperimentalSerializationApi::class)
    fun write(output: OutputStream) {
        output.write(ProtoBuf.encodeToByteArray(this))
    }
}

internal fun readFolderThumbnailMetadata(bufferedSource: BufferedSource) =
    runCatching {
        ProtoBuf.decodeFromByteArray<FolderThumbnailMetadata>(bufferedSource.readByteArray())
    }.getOrElse { FolderThumbnailMetadata() }
