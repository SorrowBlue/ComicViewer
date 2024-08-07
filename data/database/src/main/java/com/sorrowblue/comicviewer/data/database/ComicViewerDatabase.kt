package com.sorrowblue.comicviewer.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.BookshelfIdConverter
import com.sorrowblue.comicviewer.data.database.entity.FavoriteEntity
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFileEntity
import com.sorrowblue.comicviewer.data.database.entity.FavoriteIdConverter
import com.sorrowblue.comicviewer.data.database.entity.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.PasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.ReadLaterFileEntity

@Database(
    entities = [BookshelfEntity::class, FileEntity::class, FavoriteEntity::class, FavoriteFileEntity::class, ReadLaterFileEntity::class],
    version = 4,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4),
    ]
)
@TypeConverters(PasswordConverters::class, BookshelfIdConverter::class, FavoriteIdConverter::class)
internal abstract class ComicViewerDatabase : RoomDatabase() {

    abstract fun bookshelfDao(): BookshelfDao

    abstract fun fileDao(): FileDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun favoriteFileDao(): FavoriteFileDao

    abstract fun readLaterFileDao(): ReadLaterFileDao
}
