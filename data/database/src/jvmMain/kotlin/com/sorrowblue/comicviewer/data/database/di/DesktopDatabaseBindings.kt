package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DesktopCryptUtil
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(DataScope::class)
interface DesktopDatabaseBindings {
    @Binds
    private val DesktopCryptUtil.bind: CryptUtil get() = this
}
