package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

internal actual inline fun <reified T : RoomDatabase> createDatabase(): T {
    return Room.inMemoryDatabaseBuilder<T>()
        .setDriver(BundledSQLiteDriver())
        .build()
}

internal actual fun setupComicViewerDatabaseTest() {
    startKoin {
        modules(
            module {
                single<DesktopContext> { TestDesktopContext() }
            }
        )
    }
}

internal actual fun releaseComicViewerDatabaseTest() {
    stopKoin()
}
