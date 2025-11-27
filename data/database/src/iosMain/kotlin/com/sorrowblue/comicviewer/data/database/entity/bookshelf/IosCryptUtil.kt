package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import dev.zacsweers.metro.Inject

@Inject
internal class IosCryptUtil : CryptUtil {
    override fun decrypt(alias: String, encryptedText: String): String = encryptedText

    override fun encrypt(alias: String, text: String): String = text
}
