package com.sorrowblue.comicviewer.data.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionFileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.readlater.ReadLaterFileEntity

internal const val DATABASE_VERSION = 8
internal const val DATABASE_NAME = "comic_viewer_database"

@Database(
    entities = [
        BookshelfEntity::class,
        FileEntity::class,
        CollectionEntity::class,
        CollectionFileEntity::class,
        ReadLaterFileEntity::class
    ],
    version = DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4),
        AutoMigration(4, 5),
        AutoMigration(5, 6),
        AutoMigration(6, 7),
        AutoMigration(7, 8, ComicViewerDatabase.AutoMigration7to8::class),
    ],
    exportSchema = true
)
@ConstructedBy(ComicViewerDatabaseConstructor::class)
@TypeConverters(DecryptedPasswordConverters::class)
internal abstract class ComicViewerDatabase : RoomDatabase() {

    abstract fun bookshelfDao(): BookshelfDao

    abstract fun fileDao(): FileDao

    abstract fun collectionDao(): CollectionDao

    abstract fun collectionFileDao(): CollectionFileDao

    abstract fun readLaterFileDao(): ReadLaterFileDao

    @DeleteTable(tableName = "favorite_file")
    @DeleteTable(tableName = "favorite")
    class AutoMigration7to8 : AutoMigrationSpec

    class ManualMigration7to8 : Migration(7, 8) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                        INSERT INTO
                          collection(id, name, type, bookshelf_id, query, range, range_parent, period, sort_type, sort_type_asc, show_hidden)
                        SELECT
                          id,
                          name,
                          'Basic' as type,
                          null as bookshelf_id,
                          null as query,
                          null as range,
                          null as range_parent,
                          null as period,
                          null as sort_type,
                          null as sort_type_asc,
                          null as show_hidden
                        FROM
                          favorite
                        ;
                """.trimIndent()
            )
            connection.execSQL(
                """
                        INSERT INTO
                          collection_file(collection_id, bookshelf_id, file_path)
                        SELECT
                          favorite_id,
                          bookshelf_id,
                          file_path
                        FROM
                          favorite_file
                        ;
                """.trimIndent()
            )
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
internal expect object ComicViewerDatabaseConstructor :
    RoomDatabaseConstructor<ComicViewerDatabase>
