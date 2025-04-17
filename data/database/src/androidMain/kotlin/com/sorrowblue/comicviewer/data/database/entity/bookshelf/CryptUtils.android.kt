package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher

internal actual object CryptUtils {

    private const val PROVIDER = "AndroidKeyStore"

    private const val CIPHER_TRANSFORMATION =
        "${KeyProperties.KEY_ALGORITHM_RSA}/${KeyProperties.BLOCK_MODE_ECB}/${KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1}"

    actual fun decrypt(alias: String, encryptedText: String): String? {
        val keyStore = KeyStore.getInstance(PROVIDER)
        keyStore.load(null)
        if (!keyStore.containsAlias(alias)) {
            return null
        }
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keyStore.getKey(alias, null))
        val bytes = Base64.decode(encryptedText, Base64.URL_SAFE)
        val b = cipher.doFinal(bytes)
        return b.decodeToString()
    }

    actual fun encrypt(alias: String, text: String): String {
        val keyStore = KeyStore.getInstance(PROVIDER)
        keyStore.load(null)
        if (!keyStore.containsAlias(alias)) {
            val keyPairGenerator =
                KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, PROVIDER)
            keyPairGenerator.initialize(createKeyPairGeneratorSpec(alias))
            keyPairGenerator.generateKeyPair()
        }
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, keyStore.getCertificate(alias).publicKey)
        val bytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.URL_SAFE)
    }

    private fun createKeyPairGeneratorSpec(alias: String): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()
    }
}
