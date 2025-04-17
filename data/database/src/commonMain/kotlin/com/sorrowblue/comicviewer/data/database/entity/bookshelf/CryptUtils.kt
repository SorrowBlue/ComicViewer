package com.sorrowblue.comicviewer.data.database.entity.bookshelf

internal interface CryptUtil {
    fun decrypt(alias: String, encryptedText: String): String?

    fun encrypt(alias: String, text: String): String
}

internal expect object CryptUtils {

    fun decrypt(alias: String, encryptedText: String): String?

    fun encrypt(alias: String, text: String): String
}
