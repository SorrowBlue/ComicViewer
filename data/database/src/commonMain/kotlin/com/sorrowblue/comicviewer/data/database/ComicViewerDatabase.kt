package com.sorrowblue.comicviewer.data.database

import androidx.room3.AutoMigration
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.DeleteTable
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.TypeConverters
import androidx.room3.migration.AutoMigrationSpec
import androidx.room3.migration.Migration
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

internal const val DatabaseVersion = 10
internal const val DatabaseName = "comic_viewer_database"

@Database(
    entities = [
        BookshelfEntity::class,
        FileEntity::class,
        CollectionEntity::class,
        CollectionFileEntity::class,
        ReadLaterFileEntity::class,
    ],
    version = DatabaseVersion,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4),
        AutoMigration(4, 5),
        AutoMigration(5, 6),
        AutoMigration(6, 7),
        AutoMigration(7, 8, ComicViewerDatabase.AutoMigration7to8::class),
        AutoMigration(8, 9),
        AutoMigration(9, 10),
    ],
    exportSchema = true,
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
        override suspend fun migrate(connection: SQLiteConnection) {
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
                """.trimIndent(),
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
                """.trimIndent(),
            )
        }
    }

    /**
     * Migration from 8 to 9.
     *
     * Update bookshelf type from 'INTERNAL' to 'DEVICE'
     */
    class ManualMigration8to9 : Migration(8, 9) {
        override suspend fun migrate(connection: SQLiteConnection) {
            connection.execSQL("UPDATE bookshelf SET type = 'DEVICE' WHERE type = 'INTERNAL'")
        }
    }

    class ManualMigration9to10 : Migration(9, 10) {
        override suspend fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                CREATE INDEX IF NOT EXISTS `index_bookshelf_deleted_id` ON bookshelf (`deleted`, `id`);
                CREATE INDEX IF NOT EXISTS `index_file_bookshelf_id_parent` ON file (`bookshelf_id`, `parent`);
                CREATE INDEX IF NOT EXISTS `index_file_bookshelf_id_file_type` ON file (`bookshelf_id`, `file_type`);
                """.trimIndent(),
            )
        }
    }
}

internal expect object ComicViewerDatabaseConstructor :
    RoomDatabaseConstructor<ComicViewerDatabase> {
    override fun initialize(): ComicViewerDatabase
}
