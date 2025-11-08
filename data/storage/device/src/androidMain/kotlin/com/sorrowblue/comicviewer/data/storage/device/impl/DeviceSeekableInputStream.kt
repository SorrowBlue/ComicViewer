package com.sorrowblue.comicviewer.data.storage.device.impl

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream

internal class DeviceSeekableInputStream(context: Context, uri: Uri) : SeekableInputStream {
    private val input = ParcelFileDescriptor.AutoCloseInputStream(
        context.contentResolver.openFileDescriptor(uri, "r"),
    )

    override fun seek(offset: Long, whence: Int): Long {
        when (whence) {
            SeekableInputStream.SeekSet -> input.channel.position(offset)
            SeekableInputStream.SeekCur -> input.channel.position(
                input.channel.position() + offset,
            )
            SeekableInputStream.SeekEnd -> input.channel.position(input.channel.size() + offset)
        }
        return input.channel.position()
    }

    override fun position(): Long = input.channel.position()

    override fun read(buf: ByteArray): Int = input.read(buf)

    override fun close() {
        input.close()
    }
}
