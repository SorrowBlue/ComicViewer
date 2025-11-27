package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(DataScope::class)
interface IosDatabaseProviders {
    @Binds
    private val IosCryptUtil.bind: CryptUtil get() = this
}
