package com.sorrowblue.comicviewer.data.coil

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

internal interface CoilMetaData {

    fun writeTo(sink: Sink)

    interface CompanionObject
}

@OptIn(ExperimentalSerializationApi::class)
internal inline fun <reified T : CoilMetaData> CoilMetaData.CompanionObject.from(source: Source) =
    ProtoBuf.decodeFromByteArray<T>(source.readByteArray())
