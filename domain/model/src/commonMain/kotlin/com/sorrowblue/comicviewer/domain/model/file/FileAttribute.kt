/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

/**
 * Represents low-level file system attributes of a file or directory.
 *
 * @property archive Whether the archive attribute is set.
 * @property compressed Whether the file is compressed.
 * @property directory Whether the entry is a directory.
 * @property normal Whether the file has no other attributes set.
 * @property readonly Whether the file is read-only.
 * @property system Whether the file is a system file.
 * @property temporary Whether the file is a temporary file.
 * @property sharedRead Whether shared read access is enabled.
 * @property hidden Whether the file is hidden.
 * @property volume Whether the entry represents a volume label.
 */
data class FileAttribute(
    val archive: Boolean = false,
    val compressed: Boolean = false,
    val directory: Boolean = false,
    val normal: Boolean = false,
    val readonly: Boolean = false,
    val system: Boolean = false,
    val temporary: Boolean = false,
    val sharedRead: Boolean = false,
    val hidden: Boolean = false,
    val volume: Boolean = false,
)
