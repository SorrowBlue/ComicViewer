package com.sorrowblue.comicviewer.domain.reader

import kotlin.math.min
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import okio.Timeout

fun RawSource.asOkioSource(): okio.Source = object : okio.Source {
    private val buffer = Buffer() // TODO: optimization - reuse Source's buffer if possible

    override fun close() = withKxIO2OkioExceptionMapping {
        this@asOkioSource.close()
    }

    override fun read(sink: okio.Buffer, byteCount: Long): Long = withKxIO2OkioExceptionMapping {
        val readBytes = this@asOkioSource.readAtMostTo(buffer, byteCount)
        if (readBytes == -1L) return -1L

        var remainingBytes = readBytes
        while (remainingBytes > 0) {
            @OptIn(UnsafeIoApi::class)
            UnsafeBufferOperations.readFromHead(buffer) { data, from, to ->
                val len = to - from
                remainingBytes -= len
                sink.write(data, from, len)
                len
            }
        }
        return readBytes
    }

    override fun timeout(): Timeout = Timeout.NONE
}

fun okio.Sink.asKotlinxIoRawSink(): RawSink = object : RawSink {
    private val buffer =
        okio.Buffer() // TODO: optimization - reuse BufferedSink's buffer if possible

    override fun write(source: Buffer, byteCount: Long) = withOkio2KxIOExceptionMapping {
        require(source.size >= byteCount) {
            "Buffer does not contain enough bytes to write. Requested $byteCount, actual size is ${source.size}"
        }
        var remaining = byteCount
        while (remaining > 0) {
            @OptIn(UnsafeIoApi::class)
            UnsafeBufferOperations.readFromHead(source) { data, from, to ->
                val toRead = min((to - from).toLong(), remaining).toInt()
                remaining -= toRead
                buffer.write(data, from, toRead)
                toRead
            }
            this@asKotlinxIoRawSink.write(buffer, byteCount)
        }
    }

    override fun flush() = withOkio2KxIOExceptionMapping {
        this@asKotlinxIoRawSink.flush()
    }

    override fun close() = withOkio2KxIOExceptionMapping {
        this@asKotlinxIoRawSink.close()
    }
}

/**
 * Returns a [kotlinx.io.RawSource] backed by this [okio.Source].
 *
 * Closing one of these sources will also close another one.
 */
public fun okio.Source.asKotlinxIoRawSource(): RawSource = object : RawSource {
    private val buffer = okio.Buffer() // TODO: optimization - reuse BufferedSource's buffer if possible

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long = withOkio2KxIOExceptionMapping {
        val readBytes = this@asKotlinxIoRawSource.read(buffer, byteCount)
        if (readBytes == -1L) return -1L

        var remaining = readBytes
        while (remaining > 0) {
            @OptIn(UnsafeIoApi::class)
            UnsafeBufferOperations.writeToTail(sink, 1) { data, from, to ->
                val toRead = min((to - from).toLong(), remaining).toInt()
                val read = buffer.read(data, from, toRead)
                check(read != -1) { "Buffer was exhausted before reading $toRead bytes from it." }
                remaining -= read
                read
            }
        }

        return readBytes
    }

    override fun close() = withOkio2KxIOExceptionMapping {
        this@asKotlinxIoRawSource.close()
    }
}
