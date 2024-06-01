package com.sorrowblue.comicviewer.data.coil

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

internal interface CoilMetaData {

    fun writeTo(sink: BufferedSink)

    interface CompanionObject
}

@OptIn(ExperimentalSerializationApi::class)
@Suppress("unused")
internal inline fun <reified T : CoilMetaData> CoilMetaData.CompanionObject.from(source: BufferedSource) =
    ProtoBuf.decodeFromByteArray<T>(source.readByteArray())
