package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room3.ColumnTypeConverter
import androidx.room3.ProvidedColumnTypeConverter
import dev.zacsweers.metro.Inject

private const val ALIAS = "library-data.password"

@ProvidedColumnTypeConverter
@Inject
internal class DecryptedPasswordConverters(private val cryptUtil: CryptUtil) {
    @ColumnTypeConverter
    fun decrypt(value: String): DecryptedPassword = DecryptedPassword(
        cryptUtil.decrypt(ALIAS, value).orEmpty(),
    )

    @ColumnTypeConverter
    fun encrypt(value: DecryptedPassword): String = cryptUtil.encrypt(ALIAS, value.plane)
}
