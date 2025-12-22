package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.entity.bookshelf.AndroidCryptUtil
import org.junit.Assert
import org.junit.Test

class CryptUtilsTest {
    @Test
    fun testEncryptDecrypt() {
        val alias = "password"
        val text = "text"
        val cryptUtil = AndroidCryptUtil()
        val encryptedText = cryptUtil.encrypt(alias, text)
        Assert.assertNotEquals(encryptedText, text)

        Assert.assertEquals(cryptUtil.decrypt(alias, encryptedText), text)
    }
}
