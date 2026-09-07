/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.file

import kotlinx.serialization.Serializable

@Serializable
sealed interface SupportExtension {
    val extension: String

    enum class Archive(override val extension: String) : SupportExtension {
        SEVEN_Z("7z"),
        CAB("cab"),
        CB7("cb7"),
        CBR("cbr"),
        CBT("cbt"),
        CBZ("cbz"),
        LZH("lzh"),
        RAR("rar"),
        TAR("tar"),
        WIM("wim"),
        ZIP("zip"),
        PDF("pdf"),
    }
}
