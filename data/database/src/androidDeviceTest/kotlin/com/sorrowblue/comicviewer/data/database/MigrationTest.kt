package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.AndroidCryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import org.junit.Rule
import org.junit.Test

// Array of all migrations.
private val ALL_MIGRATIONS = listOf(
    ComicViewerDatabase_AutoMigration_2_3_Impl(),
    ComicViewerDatabase_AutoMigration_3_4_Impl(),
    ComicViewerDatabase_AutoMigration_4_5_Impl(),
    ComicViewerDatabase_AutoMigration_5_6_Impl(),
    ComicViewerDatabase_AutoMigration_6_7_Impl(),
    ComicViewerDatabase.ManualMigration7to8(),
    ComicViewerDatabase_AutoMigration_7_8_Impl(),
)
private const val TEST_DB = "migration-test"

internal class MigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ComicViewerDatabase::class.java,
    )

    @Test
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).close()

        val database = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ComicViewerDatabase::class.java,
            TEST_DB,
        )
            .addTypeConverter(DecryptedPasswordConverters(AndroidCryptUtil())).apply {
                ALL_MIGRATIONS.forEach {
                    addMigrations(it)
                }
            }
            .build()
        database.openHelper.writableDatabase.close()
    }
}
