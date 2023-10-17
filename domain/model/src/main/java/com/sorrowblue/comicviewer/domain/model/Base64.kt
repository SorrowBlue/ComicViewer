package com.sorrowblue.comicviewer.domain.model

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
object Base64 {

    fun String.encodeToBase64(): String {
        return Base64.UrlSafe.encode(encodeToByteArray())
    }

    fun String.decodeFromBase64(): String {
        return Base64.UrlSafe.decode(encodeToByteArray()).decodeToString()
    }
}
