package com.sorrowblue.comicviewer.domain.model

enum class SupportExtension(val extension: String) {
    // 圧縮/アーカイブ
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

    // ドキュメント
    PDF("pdf"),
    EPUB("epub"),
    XPS("xps"),
    OPEN_XPS("oxps"),
}
