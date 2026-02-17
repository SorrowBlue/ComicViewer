package com.sorrowblue.comicviewer.data.storage.client

/**
 * An interface representing a data stream that supports seeking to different positions.
 * It extends [AutoCloseable] to ensure resources are properly released after use.
 */
actual interface SeekableInputStream : AutoCloseable {

    /**
     * Reads data from the stream into the provided buffer.
     *
     * @param buf the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or `-1` if there is no
     * more data because the end of the stream has been reached.
     */
    fun read(buf: ByteArray): Int

    /**
     * Sets the offset, measured from [whence], at which the next read or write occurs.
     *
     * @param offset the new position in the stream, interpreted according to [whence].
     * @param whence the reference point for the offset: [SEEK_SET] (start of stream),
     * [SEEK_CUR] (current position), or [SEEK_END] (end of stream).
     * @return the new position within the stream.
     */
    fun seek(offset: Long, whence: Int): Long

    /**
     * Returns the current position in the stream.
     *
     * @return the current offset from the beginning of the stream, in bytes.
     */
    fun position(): Long

    companion object {
        /**
         * Seek from the beginning of the stream.
         */
        const val SEEK_SET = 0

        /**
         * A seek mode to set the position relative to the current position.
         */
        const val SEEK_CUR = 1

        /**
         * Seek relative to the end of the stream.
         */
        const val SEEK_END = 2
    }
}
