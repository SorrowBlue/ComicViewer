package com.sorrowblue.comicviewer.data.database.entity.bookshelf

internal actual object CryptUtils {
    actual fun decrypt(alias: String, encryptedText: String): String? {
        return encryptedText
    }

    actual fun encrypt(alias: String, text: String): String {
        return text
    }
}
