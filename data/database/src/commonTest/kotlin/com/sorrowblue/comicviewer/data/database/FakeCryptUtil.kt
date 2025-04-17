package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil

internal class FakeCryptUtil : CryptUtil {
    override fun decrypt(alias: String, encryptedText: String): String? {
        return encryptedText
    }

    override fun encrypt(alias: String, text: String): String {
        return text
    }
}
