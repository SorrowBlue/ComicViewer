package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Single

@Single
internal class IosCryptUtil : CryptUtil {
    @OptIn(ExperimentalEncodingApi::class, ExperimentalForeignApi::class)
    override fun decrypt(alias: String, encryptedText: String): String? {
        return encryptedText
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun encrypt(alias: String, text: String): String {
        return text
    }
}
