package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import dev.zacsweers.metro.createGraph
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest

/**
 * Unit tests for boundary cases and edge scenarios in SMB connections.
 *
 * These tests verify:
 * - Minimum and maximum values for configuration parameters
 * - Concurrent access patterns
 * - Edge cases in authentication
 * - Boundary conditions for paths and file sizes
 */
@Suppress("UNCHECKED_CAST")
class SmbFileClientBoundaryTest {
    private val graph = createGraph<TestGraph>()

    /**
     * Tests minimum valid port number (1).
     */
    @Test
    fun testMinimumPortNumber() {
        val server = SmbServer(
            "Min Port Server",
            "10.0.2.2",
            1,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals(1, (client.bookshelf as SmbServer).port)
    }

    /**
     * Tests maximum valid port number (65535).
     */
    @Test
    fun testMaximumPortNumber() {
        val server = SmbServer(
            "Max Port Server",
            "10.0.2.2",
            65535,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals(65535, (client.bookshelf as SmbServer).port)
    }

    /**
     * Tests standard SMB port (445).
     */
    @Test
    fun testStandardSmbPort() {
        val server = SmbServer(
            "Standard Port Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertEquals(445, (client.bookshelf as SmbServer).port)
    }

    /**
     * Tests legacy SMB port (139).
     */
    @Test
    fun testLegacySmbPort() {
        val server = SmbServer(
            "Legacy Port Server",
            "10.0.2.2",
            139,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertEquals(139, (client.bookshelf as SmbServer).port)
    }

    /**
     * Tests that very long username is handled.
     */
    @Test
    fun testVeryLongUsername() {
        val longUsername = "u".repeat(500)
        val server = SmbServer(
            "Long Username Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", longUsername, "pass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals(500, auth.username.length)
    }

    /**
     * Tests that very long password is handled.
     */
    @Test
    fun testVeryLongPassword() {
        val longPassword = "p".repeat(500)
        val server = SmbServer(
            "Long Password Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "user", longPassword),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals(500, auth.password.length)
    }

    /**
     * Tests that very long domain name is handled.
     */
    @Test
    fun testVeryLongDomain() {
        val longDomain = "DOMAIN" + "D".repeat(494) // Total 500 chars
        val server = SmbServer(
            "Long Domain Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword(longDomain, "user", "pass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals(500, auth.domain.length)
    }

    /**
     * Tests that very long hostname is handled.
     */
    @Test
    fun testVeryLongHostname() {
        val longHostname = "host" + "h".repeat(246) + ".com" // Total 254 chars (max DNS)
        val server = SmbServer(
            "Long Hostname Server",
            longHostname,
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals(254, (client.bookshelf as SmbServer).host.length)
    }

    /**
     * Tests that very long display name is handled.
     */
    @Test
    fun testVeryLongDisplayName() {
        val longDisplayName = "Display Name " + "n".repeat(500)
        val server = SmbServer(
            longDisplayName,
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertTrue(client.bookshelf.displayName.length > 500)
    }

    /**
     * Tests creating multiple clients concurrently.
     */
    @Test
    fun testConcurrentClientCreation() = runTest {
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>

        val clients = (1..10).map { i ->
            async {
                val server = SmbServer(
                    "Concurrent Server $i",
                    "10.0.2.$i",
                    445,
                    SmbServer.Auth.Guest,
                )
                factory.create(server)
            }
        }.awaitAll()

        assertEquals(10, clients.size)
        clients.forEach { client ->
            assertNotNull(client)
        }
    }

    /**
     * Tests that minimum bookshelf ID is handled.
     */
    @Test
    fun testMinimumBookshelfId() {
        val bookshelfId = BookshelfId(0)
        val server = SmbServer(
            bookshelfId,
            "Min ID Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
            fileCount = 0,
            isDeleted = false,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertEquals(bookshelfId, client.bookshelf.id)
    }

    /**
     * Tests that maximum bookshelf ID is handled.
     */
    @Test
    fun testMaximumBookshelfId() {
        val bookshelfId = BookshelfId(Int.MAX_VALUE)
        val server = SmbServer(
            bookshelfId,
            "Max ID Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
            fileCount = 0,
            isDeleted = false,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertEquals(bookshelfId, client.bookshelf.id)
    }

    /**
     * Tests that single character strings are handled in authentication.
     */
    @Test
    fun testSingleCharacterCredentials() {
        val server = SmbServer(
            "Single Char Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("D", "u", "p"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals("D", auth.domain)
        assertEquals("u", auth.username)
        assertEquals("p", auth.password)
    }

    /**
     * Tests that special characters in credentials are preserved.
     */
    @Test
    fun testSpecialCharactersInCredentials() {
        val server = SmbServer(
            "Special Chars Credentials",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword(
                "DOMAIN-01",
                "user@domain.com",
                "p@ssw0rd!#$%",
            ),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals("DOMAIN-01", auth.domain)
        assertEquals("user@domain.com", auth.username)
        assertEquals("p@ssw0rd!#$%", auth.password)
    }

    /**
     * Tests that localhost addresses are handled.
     */
    @Test
    fun testLocalhostAddress() {
        val server = SmbServer(
            "Localhost Server",
            "127.0.0.1",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals("127.0.0.1", (client.bookshelf as SmbServer).host)
    }

    /**
     * Tests that localhost hostname is handled.
     */
    @Test
    fun testLocalhostHostname() {
        val server = SmbServer(
            "Localhost Name Server",
            "localhost",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals("localhost", (client.bookshelf as SmbServer).host)
    }

    /**
     * Tests that zero file count is handled.
     */
    @Test
    fun testZeroFileCount() {
        val bookshelfId = BookshelfId(1)
        val server = SmbServer(
            bookshelfId,
            "Zero Files Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
            fileCount = 0,
            isDeleted = false,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertEquals(0, client.bookshelf.fileCount)
    }

    /**
     * Tests that large file count is handled.
     */
    @Test
    fun testLargeFileCount() {
        val bookshelfId = BookshelfId(1)
        val server = SmbServer(
            bookshelfId,
            "Large File Count Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
            fileCount = 1000000,
            isDeleted = false,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertEquals(1000000, client.bookshelf.fileCount)
    }

    /**
     * Tests that deleted bookshelf flag is preserved.
     */
    @Test
    fun testDeletedBookshelfFlag() {
        val bookshelfId = BookshelfId(1)
        val server = SmbServer(
            bookshelfId,
            "Deleted Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
            fileCount = 0,
            isDeleted = true,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertTrue(client.bookshelf.isDeleted)
    }
}
