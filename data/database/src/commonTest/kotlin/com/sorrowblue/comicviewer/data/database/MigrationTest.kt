package com.sorrowblue.comicviewer.data.database

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.execSQL
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

internal const val TEST_DB_NAME = "migration-test"

internal expect val AutoMigration_2_3_Impl: Migration
internal expect val AutoMigration_3_4_Impl: Migration
internal expect val AutoMigration_4_5_Impl: Migration
internal expect val AutoMigration_5_6_Impl: Migration
internal expect val AutoMigration_6_7_Impl: Migration

internal expect fun getMigrationTestHelper(): MigrationTestHelper

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class MigrationTest : TestMan() {

    @Test
    fun testMigration_2_3() = runTest {
        migrationTest(
            version = 2,
            migration = AutoMigration_2_3_Impl,
            before = {
                execSQL(
                    """
                        |INSERT INTO
                        |  file (path, bookshelf_id, name, parent, size, last_modified, file_type, file_type_order, sort_index, cache_key, total_page_count, last_read_page, last_read)
                        |  VALUES ('test_path', 1, 'test_name', 'test_parent', 1, 1, 'file', 1, 1, 'test_cache_key', 1, 1, 1)
                    """.trimMargin()
                )
            },
            after = {
                prepare("SELECT hidden FROM file").use {
                    assertTrue(it.step())
                    assertFalse(it.getBoolean(0))
                }
            },
        )
    }

    @Test
    fun testMigration_3_4() = runTest {
        migrationTest(
            version = 3,
            migration = AutoMigration_3_4_Impl,
            before = {
                execSQL(
                    """
                    |INSERT INTO
                    | favorite (id, name)
                    | VALUES (1, 'test_name')
                """.trimMargin()
                )
            },
            after = {
                prepare("SELECT added_date_time FROM favorite").use {
                    assertTrue(it.step())
                    assertEquals(it.getInt(0), -1)
                }
            }
        )
    }

    @Test
    fun testMigration_4_5() = runTest {
        migrationTest(
            version = 4,
            migration = AutoMigration_4_5_Impl,
            before = {
                execSQL(
                    """
                    |INSERT INTO
                    | read_later_file (file_path, bookshelf_id)
                    | VALUES ('test_path', 1)
                """.trimMargin()
                )
            },
            after = {
                prepare("SELECT modified_date FROM read_later_file").use {
                    assertTrue(it.step())
                    assertEquals(it.getInt(0), -1)
                }
            }
        )
    }

    @Test
    fun testMigration_5_6() = runTest {
        migrationTest(
            version = 5,
            migration = AutoMigration_5_6_Impl,
            before = {
                execSQL(
                    """
                    |INSERT INTO
                    | bookshelf (id, display_name, type, host, port, domain, username, password)
                    | VALUES (1, 'test_name', 'smb', 'test_host', 445, 'test_domain', 'test_username', 'test_password')
                """.trimMargin()
                )
            },
            after = {
                prepare("SELECT deleted FROM bookshelf").use {
                    assertTrue(it.step())
                    assertFalse(it.getBoolean(0))
                }
            }
        )
    }

    @Test
    fun testMigration_6_7() = runTest {
        migrationTest(
            version = 6,
            migration = AutoMigration_6_7_Impl,
            before = {
                prepare("SELECT COUNT(*) FROM sqlite_master WHERE TYPE='table' AND name='collection'").use {
                     assertTrue(it.step())
                    assertEquals(it.getInt(0), 0)
                }
            },
            after = {
                prepare("SELECT COUNT(*) FROM sqlite_master WHERE TYPE='table' AND name='collection'").use {
                    assertTrue(it.step())
                    assertEquals(it.getInt(0), 1)
                }
            }
        )
    }


    @Test
    fun testMigration_7_8() = runTest {
        migrationTest(
            version = 7,
            migration = ComicViewerDatabase.ManualMigration7to8(),
            before = {
                execSQL(
                    """
                        INSERT INTO bookshelf (display_name, type, host, port, domain, username, password)
                        VALUES ('display_name', 'INTERNAL', 'host', 0, '', '', '');
                """.trimIndent()
                )
                execSQL(
                    """
                        INSERT INTO file (path, bookshelf_id, name, parent, size, last_modified, file_type, file_type_order, sort_index, cache_key, total_page_count, last_read_page, last_read)
                        VALUES ('path1', 1, 'name', 'parent', 0, 0, 'FILE', 1, 0, '', 0, 0, 0),
                               ('path2', 1, 'name', 'parent', 0, 0, 'FILE', 1, 0, '', 0, 0, 0);
                """.trimIndent()
                )
                execSQL(
                    """
                        INSERT INTO favorite (name)
                        VALUES ('test_collection_name1'), ('test_collection_name2');
                """.trimIndent()
                )
                execSQL(
                    """
                        INSERT INTO favorite_file (favorite_id, bookshelf_id, file_path)
                        VALUES (1, 1, 'path1'), (2, 1, 'path2');
                """.trimIndent()
                )
            },
        ) {
            prepare("SELECT * FROM collection").use { statement ->
                assertTrue(statement.step())
                val columns = statement.getColumnNames()
                assertEquals(statement.getInt(columns.indexOf("id")), 1)
                assertEquals(statement.getText("name"), "test_collection_name1")
                assertEquals(statement.getText("type"), "Basic")
                assertEquals(statement.getInt(columns.indexOf("bookshelf_id")), 0)
                assertEquals(statement.getText("query"), null)
                assertEquals(statement.getText("range"), null)
                assertEquals(statement.getText("range_parent"), null)
                assertEquals(statement.getText("period"), null)
                assertEquals(statement.getText("sort_type"), null)
                assertEquals(statement.getText("sort_type_asc"), null)
                assertEquals(statement.getText("show_hidden"), null)
            }
            prepare("SELECT * FROM collection_file").use { statement ->
                assertTrue(statement.step())
                statement.getColumnNames()
                assertEquals(statement.getInt("collection_id"), 1)
                assertEquals(statement.getInt("bookshelf_id"), 1)
                assertEquals(statement.getText("file_path"), "path1")
            }
        }
    }

    private fun SQLiteStatement.getText(column: String): String? =
        getColumnNames().indexOf(column).let { index ->
            if (isNull(index)) null else getText(index)
        }

    private fun SQLiteStatement.getInt(column: String): Int? =
        getColumnNames().indexOf(column).let { index ->
            if (isNull(index)) null else getInt(index)
        }

    private fun migrationTest(
        version: Int,
        migration: Migration,
        before: SQLiteConnection.() -> Unit,
        after: SQLiteConnection.() -> Unit,
    ) {
        val migrationTestHelper = getMigrationTestHelper()
        migrationTestHelper.createDatabase(version).use { connection ->
            before(connection)
        }
        migrationTestHelper.runMigrationsAndValidate(version + 1, listOf(migration))
            .use { connection ->
                after.invoke(connection)
            }
    }
}
