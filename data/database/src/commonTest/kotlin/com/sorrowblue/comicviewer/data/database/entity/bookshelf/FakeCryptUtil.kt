package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(DataScope::class)
@Inject
internal class FakeCryptUtil : CryptUtil {
    override fun decrypt(alias: String, encryptedText: String): String? = encryptedText

    override fun encrypt(alias: String, text: String): String = text
}
