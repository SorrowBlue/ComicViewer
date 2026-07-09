/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database.entity.bookshelf

internal interface CryptUtil {
    fun decrypt(alias: String, encryptedText: String): String?

    fun encrypt(alias: String, text: String): String
}
