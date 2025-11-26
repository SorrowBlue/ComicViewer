package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject

@ContributesTo(DataScope::class)
interface IosDatabaseBindings {
    @Binds
    private val IosCryptUtil.bind: CryptUtil get() = this
}

@Inject
internal class IosCryptUtil : CryptUtil {
    override fun decrypt(alias: String, encryptedText: String): String? = encryptedText

    override fun encrypt(alias: String, text: String): String = text
}
