package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class CryptUtilsTest {

    @Test
    fun testEncryptDecrypt() {
        val alias = "password"
        val text = "text"
        val encryptedText = CryptUtils.encrypt(alias, text)
        Assert.assertNotEquals(encryptedText, text)

        Assert.assertEquals(CryptUtils.decrypt(alias, encryptedText), text)
    }
}
