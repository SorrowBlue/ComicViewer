package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.AndroidCryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import org.junit.Rule
import org.junit.Test

internal class MigrationTest2 {

    // Array of all migrations.
    private val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        AutoMigration_2_3_Impl,
        AutoMigration_3_4_Impl,
        AutoMigration_4_5_Impl,
        AutoMigration_5_6_Impl,
        AutoMigration_6_7_Impl,
        ComicViewerDatabase.ManualMigration7to8(),
        AutoMigration_7_8_Impl,
    )
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ComicViewerDatabase::class.java,
    )

    @Test
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ComicViewerDatabase::class.java,
            TEST_DB
        )
            .addTypeConverter(DecryptedPasswordConverters(AndroidCryptUtil()))
            .addMigrations(*ALL_MIGRATIONS).build().apply {
                openHelper.writableDatabase.close()
            }
    }
}

internal val AutoMigration_2_3_Impl: Migration =
    ComicViewerDatabase_AutoMigration_2_3_Impl()

internal val AutoMigration_3_4_Impl: Migration =
    ComicViewerDatabase_AutoMigration_3_4_Impl()

internal val AutoMigration_4_5_Impl: Migration =
    ComicViewerDatabase_AutoMigration_4_5_Impl()

internal val AutoMigration_5_6_Impl: Migration =
    ComicViewerDatabase_AutoMigration_5_6_Impl()

internal val AutoMigration_6_7_Impl: Migration =
    ComicViewerDatabase_AutoMigration_6_7_Impl()

internal val AutoMigration_7_8_Impl: Migration =
    ComicViewerDatabase_AutoMigration_7_8_Impl()
