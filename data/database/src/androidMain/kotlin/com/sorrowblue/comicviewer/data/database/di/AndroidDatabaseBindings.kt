package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.entity.bookshelf.AndroidCryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(DataScope::class)
interface AndroidDatabaseBindings {
    @Binds
    private val AndroidCryptUtil.bind: CryptUtil get() = this
}
