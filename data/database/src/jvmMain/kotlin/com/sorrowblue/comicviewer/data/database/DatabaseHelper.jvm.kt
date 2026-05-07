package com.sorrowblue.comicviewer.data.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.resolve
import logcat.logcat

@Inject
internal actual class DatabaseHelper actual constructor(private val context: PlatformContext) {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val dbPath = FileKit.databasesDir.also {
            it.createDirectories()
            logcat { "getDatabaseBuilder path=${it.absolutePath()}" }
        }
        return Room.databaseBuilder<ComicViewerDatabase>(
            name = dbPath.resolve(DatabaseName).absolutePath(),
        ).setDriver(BundledSQLiteDriver())
    }
}
