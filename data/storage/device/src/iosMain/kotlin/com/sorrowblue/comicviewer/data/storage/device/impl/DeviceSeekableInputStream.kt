package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell

@OptIn(ExperimentalForeignApi::class)
internal class DeviceSeekableInputStream(private val filePath: String) : SeekableInputStream {

    private var file: CPointer<FILE>? = null

    override fun open() {
        if (file == null) {
            file = fopen(filePath, "rb")
            if (file == null) {
                println("Failed to open file: $filePath")
            }
        }
    }

    override fun read(pointer: COpaquePointer, count: Int): Int {
        val currentFile = file ?: return -1
        val bytesRead = fread(pointer, 1u, count.toULong(), currentFile)
        return bytesRead.toInt()
    }

    override fun seek(offset: Long, whence: Int): Long {
        val currentFile = file ?: return -1
        if (fseek(currentFile, offset, whence) == 0) {
            return position()
        }
        return -1
    }

    override fun position(): Long {
        val currentFile = file ?: return 0
        return ftell(currentFile)
    }

    override fun size(): Long {
        val currentFile = file ?: return 0
        val currentPos = ftell(currentFile)
        fseek(currentFile, 0, SeekableInputStream.SEEK_END)
        val fileSize = ftell(currentFile)
        fseek(currentFile, currentPos, SeekableInputStream.SEEK_SET)
        return fileSize
    }

    override fun close() {
        file?.let {
            fclose(it)
            file = null
        }
    }
}
