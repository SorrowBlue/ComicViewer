package com.sorrowblue.comicviewer.data.database.entity.bookshelf

internal expect object CryptUtils {

    fun decrypt(alias: String, encryptedText: String): String?

    fun encrypt(alias: String, text: String): String
}
