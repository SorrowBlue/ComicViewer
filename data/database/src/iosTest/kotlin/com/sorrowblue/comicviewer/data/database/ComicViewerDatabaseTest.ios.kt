package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

internal actual inline fun <reified T : RoomDatabase> createDatabase(): T {
    return Room.inMemoryDatabaseBuilder<T>().build()
}

internal actual fun setupComicViewerDatabaseTest() {
}

internal actual fun releaseComicViewerDatabaseTest() {
}
