package com.sorrowblue.comicviewer.data.remote.client.device

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.sorrowblue.comicviewer.data.remote.reader.SeekableInputStream

internal class DeviceSeekableInputStream(context: Context, uri: Uri) :
    SeekableInputStream {

    private val input = ParcelFileDescriptor.AutoCloseInputStream(
        context.contentResolver.openFileDescriptor(uri, "r")
    )

    override fun seek(offset: Long, whence: Int): Long {
        when (whence) {
            SeekableInputStream.SEEK_SET -> input.channel.position(offset)
            SeekableInputStream.SEEK_CUR -> input.channel.position(input.channel.position() + offset)
            SeekableInputStream.SEEK_END -> input.channel.position(input.channel.size() + offset)
        }
        return input.channel.position()
    }

    override fun position(): Long {
        return input.channel.position()
    }

    override fun read(buf: ByteArray): Int {
        return input.read(buf)
    }

    override fun close() {
        input.close()
    }
}
