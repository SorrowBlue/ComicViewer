package com.sorrowblue.comicviewer.domain.model

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
    }

    enum class Document(override val extension: String) : SupportExtension {
        PDF("pdf"),
        EPUB("epub"),
        XPS("xps"),
        OPEN_XPS("oxps"),
        MOBI("mobi"),
        FB2("fb2"),
    }
}
