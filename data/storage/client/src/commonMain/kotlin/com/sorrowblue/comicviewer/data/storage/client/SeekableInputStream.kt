package com.sorrowblue.comicviewer.data.storage.client

/**
 * An [AutoCloseable] input stream that supports random access by seeking to specific
 * positions within the data source.
 *
 * This interface provides a common abstraction for stream-based reading operations
 * where the current read pointer can be moved forward or backward.
 */
expect interface SeekableInputStream : AutoCloseable
