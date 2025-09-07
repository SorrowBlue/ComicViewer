package com.sorrowblue.comicviewer.data.database.entity.bookshelf

internal interface CryptUtil {
    fun decrypt(alias: String, encryptedText: String): String?

    fun encrypt(alias: String, text: String): String
}
