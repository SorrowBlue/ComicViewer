package com.sorrowblue.comicviewer.framework.navigation

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.io.readCodePointValue
import kotlinx.io.writeCodePointValue
import kotlinx.io.writeString
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.InternalJsonReaderCodePointImpl
import kotlinx.serialization.json.internal.InternalJsonWriter
import kotlinx.serialization.json.internal.decodeByReader
import kotlinx.serialization.json.internal.encodeByWriter
import kotlinx.serialization.serializer

inline fun <reified T> kSerializerType(json: Json = Json) =
    KSerializerByteArray(json.serializersModule.serializer<T>())

@OptIn(ExperimentalSerializationApi::class)
class KSerializerByteArray<T>(val serializer: KSerializer<T>) {

    fun toByteArray(value: T): ByteArray =
        Buffer().also { Json.encodeToBuffer(serializer, value, it) }.use {
            it.readByteArray()
        }

    fun fromByteArray(bytes: ByteArray): T =
        Buffer().use {
            it.write(bytes)
            Json.decodeFromBuffer(serializer, it)
        }
}

@ExperimentalSerializationApi
fun <T> Json.encodeToBuffer(
    serializer: SerializationStrategy<T>,
    value: T,
    buffer: Buffer,
) {
    val writer = JsonTokotlinIoWriter(buffer)
    try {
        encodeByWriter(this, writer, serializer, value)
    } finally {
        writer.release()
    }
}

@ExperimentalSerializationApi
fun <T> Json.decodeFromBuffer(
    deserializer: DeserializationStrategy<T>,
    source: Buffer,
): T {
    return decodeByReader(this, deserializer, KotlinSerialReader(source))
}

private const val QUOTE_CODE = '"'.code

internal class JsonTokotlinIoWriter(private val buffer: Buffer) : InternalJsonWriter {
    override fun writeLong(value: Long) {
        write(value.toString())
    }

    override fun writeChar(char: Char) {
        buffer.writeCodePointValue(char.code)
    }

    override fun write(text: String) {
        buffer.writeString(string = text)
    }

    override fun writeQuoted(text: String) {
        buffer.writeCodePointValue(QUOTE_CODE)
        InternalJsonWriter.doWriteEscaping(text) { s, start, end ->
            buffer.writeString(
                string = s,
                start,
                end
            )
        }
        buffer.writeCodePointValue(QUOTE_CODE)
    }

    override fun release() {
        // no-op, see https://github.com/Kotlin/kotlinx.serialization/pull/1982#discussion_r915043700
    }
}

internal class KotlinSerialReader(private val buffer: Buffer) :
    InternalJsonReaderCodePointImpl() {
    override fun exhausted(): Boolean = buffer.exhausted()
    override fun nextCodePoint(): Int = buffer.readCodePointValue()
}
