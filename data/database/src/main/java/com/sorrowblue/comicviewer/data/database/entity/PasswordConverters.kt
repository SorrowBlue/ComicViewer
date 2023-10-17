package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.TypeConverter

internal object PasswordConverters {

    private const val ALIAS = "library-data.password"

    @TypeConverter
    @JvmStatic
    fun String.decrypt() = DecryptedPasswordEntity(CryptUtils.decrypt(ALIAS, this).orEmpty())

    @TypeConverter
    @JvmStatic
    fun DecryptedPasswordEntity.encrypt() = CryptUtils.encrypt(ALIAS, plane)

}
