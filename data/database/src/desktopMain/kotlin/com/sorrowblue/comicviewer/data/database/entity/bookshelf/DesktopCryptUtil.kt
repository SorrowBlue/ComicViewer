package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
private const val IV = "1234567812345678"

@ContributesTo(DataScope::class)
interface DesktopDatabaseBindings {
    @Binds
    @Suppress("UnusedPrivateProperty")
    private val DesktopCryptUtil.bind: CryptUtil get() = this
}

@ContributesBinding(DataScope::class)
@Inject
internal class DesktopCryptUtil(private val context: PlatformContext) : CryptUtil {
    override fun decrypt(alias: String, encryptedText: String): String? {
        val key = loadKeyStore(alias)
        val iv = IvParameterSpec(IV.toByteArray())
        val decrypter = Cipher.getInstance(CIPHER_TRANSFORMATION)
        decrypter.init(Cipher.DECRYPT_MODE, key, iv)
        return String(decrypter.doFinal(Base64.decode(encryptedText)))
    }

    override fun encrypt(alias: String, text: String): String {
        val key = this.loadKeyStore(alias)
        val iv = IvParameterSpec(IV.toByteArray())
        val encrypter = Cipher.getInstance(CIPHER_TRANSFORMATION)
        encrypter.init(Cipher.ENCRYPT_MODE, key, iv)
        return Base64.encode(encrypter.doFinal(text.toByteArray()))
    }

    private fun loadKeyStore(alias: String): Key? {
        val keystoreFile =
            context.filesDir
                .resolve("keystore")
                .also { it.createDirectories() }
                .resolve("pass.dat")
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        if (keystoreFile.exists()) {
            keystoreFile.inputStream().use {
                keyStore.load(it, productID.toCharArray())
            }
        } else {
            keyStore.load(null, productID.toCharArray())
            if (!keyStore.isKeyEntry(alias)) {
                val aes = KeyGenerator.getInstance("AES")
                keyStore.setKeyEntry(alias, aes.generateKey(), productID.toCharArray(), null)
            }
            keystoreFile.outputStream().use {
                keyStore.store(it, productID.toCharArray())
            }
        }
        return keyStore.getKey(alias, productID.toCharArray())
    }

    private val productID by lazy {
        when {
            isWindows -> windowsProductID()
            isLinux -> linuxProductID()
            isMac -> macProductID()
            else -> "unknown"
        }
    }

    private fun macProductID(): String {
        TODO("Not yet implemented")
    }

    private fun linuxProductID(): String {
        TODO("Not yet implemented")
    }

    private fun windowsProductID(): String {
        var productID = ""
        val process = Runtime
            .getRuntime()
            .exec(arrayOf("cmd", "/C", "chcp 65001 | systeminfo | findstr /i \"Product ID:\""))
        val reader =
            BufferedReader(InputStreamReader(process.inputStream, Charset.forName("UTF-8")))
        var line: String?
        while ((reader.readLine().also { line = it }) != null) {
            productID =
                Regex("^.+\\s([0-9a-zA-Z\\-]+)")
                    .find(line.orEmpty())
                    ?.groups
                    ?.get(1)
                    ?.value
                    .orEmpty()
        }
        process.waitFor()
        return productID
    }

    private val os = System.getProperty("os.name").lowercase()
    private val isWindows = os.startsWith("win")
    private val isMac = os.startsWith("mac")
    private val isLinux = os.startsWith("linux")
}
