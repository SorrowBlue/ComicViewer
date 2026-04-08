package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(DataScope::class)
internal class IosCryptUtil : CryptUtil {
    override fun decrypt(alias: String, encryptedText: String): String = encryptedText

    override fun encrypt(alias: String, text: String): String = text
}
