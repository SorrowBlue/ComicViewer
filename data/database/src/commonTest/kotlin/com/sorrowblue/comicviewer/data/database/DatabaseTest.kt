package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.entity.EntityFactory
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

internal expect open class DatabaseTest() {
    protected val db: ComicViewerDatabase
    protected val platformContext: PlatformContext
    protected val factory: EntityFactory

    @BeforeTest
    fun setupDatabase()

    @AfterTest
    fun closeDatabase()
}
