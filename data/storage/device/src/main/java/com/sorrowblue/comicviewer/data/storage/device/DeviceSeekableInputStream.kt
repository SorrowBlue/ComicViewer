package com.sorrowblue.comicviewer.data.storage.device

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.net.toUri
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.reader.SeekableInputStream
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

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

internal class DeviceSeekableInputStream2 @AssistedInject constructor(
    @Assisted bookshelfModel: BookshelfModel.SmbServer,
    @Assisted path: String,
    @ApplicationContext context: Context
) : SeekableInputStream {

    @AssistedFactory
    interface Factory: SeekableInputStream.Factory<BookshelfModel.SmbServer> {
        override fun create(bookshelfModel: BookshelfModel.SmbServer, path: String): DeviceSeekableInputStream2
    }

    private val input = ParcelFileDescriptor.AutoCloseInputStream(
        context.contentResolver.openFileDescriptor(path.toUri(), "r")
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
