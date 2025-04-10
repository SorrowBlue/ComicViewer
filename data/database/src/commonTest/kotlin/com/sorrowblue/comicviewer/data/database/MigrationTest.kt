package com.sorrowblue.comicviewer.data.database

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.execSQL
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger

internal expect val AutoMigration_2_3_Impl: Migration
internal expect val AutoMigration_3_4_Impl: Migration
internal expect val AutoMigration_4_5_Impl: Migration
internal expect val AutoMigration_5_6_Impl: Migration
internal expect val AutoMigration_6_7_Impl: Migration

internal expect fun getMigrationTestHelper(): MigrationTestHelper

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class MigrationTest {

    @BeforeTest
    fun setup() {
        setupComicViewerDatabaseTest()
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    @AfterTest
    fun cleanup() {
        releaseComicViewerDatabaseTest()
    }

    @Test
    fun testMigration_2_3() = runTest {
        migration(
            version = 2,
            migration = AutoMigration_2_3_Impl,
            insertSQL = """
                            INSERT INTO
                              file (path, bookshelf_id, name, parent, size, last_modified, file_type, file_type_order, sort_index, cache_key, total_page_count, last_read_page, last_read)
                              VALUES ('test_path', 1, 'test_name', 'test_parent', 1, 1, 'file', 1, 1, 'test_cache_key', 1, 1, 1)
                        """.trimIndent(),
            prepareSQL = "SELECT hidden FROM file"
        ) {
            assertFalse(it.getBoolean(0))
        }
    }

    @Test
    fun testMigration_3_4() = runTest {
        migration(
            version = 3,
            migration = AutoMigration_3_4_Impl,
            insertSQL = """
                            INSERT INTO
                              favorite (id, name)
                              VALUES (1, 'test_name')
                        """.trimIndent(),
            prepareSQL = "SELECT added_date_time FROM favorite"
        ) {
            assertEquals(it.getInt(0), -1)
        }
    }

    @Test
    fun testMigration_4_5() = runTest {
        migration(
            version = 4,
            migration = AutoMigration_4_5_Impl,
            insertSQL = """
                            INSERT INTO
                              read_later_file (file_path, bookshelf_id)
                              VALUES ('test_path', 1)
                        """.trimIndent(),
            prepareSQL = "SELECT modified_date FROM read_later_file"
        ) {
            assertEquals(it.getInt(0), -1)
        }
    }

    @Test
    fun testMigration_5_6() = runTest {
        migration(
            version = 5,
            migration = AutoMigration_5_6_Impl,
            insertSQL = """
                            INSERT INTO
                              bookshelf (id, display_name, type, host, port, domain, username, password)
                              VALUES (1, 'test_name', 'smb', 'test_host', 445, 'test_domain', 'test_username', 'test_password')
                        """.trimIndent(),
            prepareSQL = "SELECT deleted FROM bookshelf"
        ) {
            assertFalse(it.getBoolean(0))
        }
    }

    @Test
    fun testMigration_6_7() = runTest {
        migration(
            version = 6,
            migration = AutoMigration_6_7_Impl,
            afterInsertSQL = """
                INSERT INTO
                  collection (name, type)
                  VALUES ('test_collection_name', 'Comic')
            """.trimIndent(),
            prepareSQL = "SELECT name FROM collection"
        ) {
            assertEquals(it.getText(0), "test_collection_name")
        }
    }

    private fun migration(
        version: Int,
        migration: Migration,
        insertSQL: String? = null,
        afterInsertSQL: String? = null,
        prepareSQL: String,
        assert: (SQLiteStatement) -> Unit,
    ) {
        val migrationTestHelper = getMigrationTestHelper()
        migrationTestHelper.createDatabase(version).use { connection ->
            insertSQL?.let(connection::execSQL)
        }
        migrationTestHelper.runMigrationsAndValidate(version + 1, listOf(migration))
            .use { connection ->
                afterInsertSQL?.let(connection::execSQL)
                connection.prepare(prepareSQL).use { stmt ->
                    assertTrue(stmt.step())
                    assert(stmt)
                }
            }
    }
}
