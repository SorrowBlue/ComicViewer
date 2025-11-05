package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import dev.zacsweers.metro.Inject

private const val ALIAS = "library-data.password"

@ProvidedTypeConverter
@Inject
internal class DecryptedPasswordConverters(private val cryptUtil: CryptUtil) {
    @TypeConverter
    fun decrypt(value: String): DecryptedPassword = DecryptedPassword(
        cryptUtil.decrypt(ALIAS, value).orEmpty(),
    )

    @TypeConverter
    fun encrypt(value: DecryptedPassword): String = cryptUtil.encrypt(ALIAS, value.plane)
}
