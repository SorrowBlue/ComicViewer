/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import kotlinx.serialization.Serializable

/**
 * Represents a file extension supported as a comic book container.
 */
@Serializable
sealed interface SupportExtension {
    /** The lowercase file extension string (without leading dot). */
    val extension: String

    /**
     * Supported archive and document file extensions.
     *
     * @property extension The lowercase file extension string.
     */
    @Serializable
    enum class Archive(override val extension: String) : SupportExtension {
        /** 7-Zip archive (`.7z`). */
        SEVEN_Z("7z"),

        /** Windows Cabinet archive (`.cab`). */
        CAB("cab"),

        /** 7-Zip comic book archive (`.cb7`). */
        CB7("cb7"),

        /** RAR comic book archive (`.cbr`). */
        CBR("cbr"),

        /** TAR comic book archive (`.cbt`). */
        CBT("cbt"),

        /** ZIP comic book archive (`.cbz`). */
        CBZ("cbz"),

        /** LZH archive (`.lzh`). */
        LZH("lzh"),

        /** RAR archive (`.rar`). */
        RAR("rar"),

        /** TAR archive (`.tar`). */
        TAR("tar"),

        /** Windows Imaging Format archive (`.wim`). */
        WIM("wim"),

        /** ZIP archive (`.zip`). */
        ZIP("zip"),

        /** PDF document (`.pdf`). */
        PDF("pdf"),
    }
}
