package com.sorrowblue.comicviewer.plugin.aidl;

/**
 * Interface for seekable input stream operations, such as reading, seeking, and closing.
 */
interface ISeekableInputStream {

    /**
     * Reads bytes into the buffer and returns the number of bytes read.
     *
     * @param buf Buffer to read bytes into
     * @return Number of bytes read
     */
    int read(inout byte[] buf);

    /**
     * Seeks to the specified offset based on whence and returns the new position.
     *
     * @param offset Offset to seek
     * @param whence Seek mode
     * @return New position in the stream
     */
    long seek(long offset, int whence);

    /**
     * Returns the current position in the stream.
     *
     * @return Current position
     */
    long position();

    /**
     * Closes the input stream.
     */
    void close();
}
