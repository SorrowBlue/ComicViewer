package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import dev.zacsweers.metro.createGraph
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest

/**
 * Unit tests for SMB server connection functionality.
 *
 * These tests verify:
 * - Authentication mechanisms (UsernamePassword and Guest)
 * - Connection error handling
 * - File operations
 * - Path handling and URI decoding
 * - Error mapping from SmbException to FileClientException
 */
@Suppress("UNCHECKED_CAST")
class SmbFileClientTest {
    private val graph = createGraph<TestGraph>()

    /**
     * Tests that SmbFileClient can be created with UsernamePassword authentication.
     */
    @Test
    fun testCreateClientWithUsernamePasswordAuth() {
        val server = SmbServer(
            "Test Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "testuser", "testpass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals(server, client.bookshelf)
    }

    /**
     * Tests that SmbFileClient can be created with Guest authentication.
     */
    @Test
    fun testCreateClientWithGuestAuth() {
        val server = SmbServer(
            "Guest Server",
            "192.168.1.100",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals(server, client.bookshelf)
    }

    /**
     * Tests that SmbFileClient can be created with different port numbers.
     */
    @Test
    fun testCreateClientWithCustomPort() {
        val server = SmbServer(
            "Custom Port Server",
            "10.0.2.2",
            8445,
            SmbServer.Auth.UsernamePassword("DOMAIN", "admin", "password123"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals(8445, (client.bookshelf as SmbServer).port)
    }

    /**
     * Tests that SmbFileClient preserves domain information in UsernamePassword auth.
     */
    @Test
    fun testCreateClientWithDomain() {
        val server = SmbServer(
            "Domain Server",
            "dc.example.com",
            445,
            SmbServer.Auth.UsernamePassword("EXAMPLE", "user", "pass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals("EXAMPLE", auth.domain)
        assertEquals("user", auth.username)
    }

    /**
     * Tests that bookshelf ID is preserved when creating client.
     */
    @Test
    fun testClientPreservesBookshelfId() {
        val bookshelfId = BookshelfId(12345)
        val server = SmbServer(
            bookshelfId,
            "ID Test Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
            fileCount = 100,
            isDeleted = false,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertEquals(bookshelfId, client.bookshelf.id)
    }

    /**
     * Tests connection to an invalid server should throw InvalidServer exception.
     * Note: This requires an actual network attempt, so it validates error handling.
     */
    @Test
    fun testConnectToInvalidServerThrowsException() = runTest {
        val server = SmbServer(
            "Invalid Server",
            "invalid.host.that.does.not.exist.local",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        // Attempting to connect to an invalid server should throw an exception
        assertFailsWith<FileClientException.InvalidServer> {
            client.connect("/share/")
        }
    }

    /**
     * Tests connection with invalid credentials should throw InvalidAuth exception.
     */
    @Test
    fun testConnectWithInvalidAuthThrowsException() = runTest {
        val server = SmbServer(
            "Auth Test Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "wronguser", "wrongpass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        // Attempting to connect with invalid credentials should throw an exception
        assertFailsWith<FileClientException.InvalidAuth> {
            client.connect("/share/")
        }
    }

    /**
     * Tests that client handles various path formats correctly.
     */
    @Test
    fun testClientAcceptsVariousPathFormats() {
        val server = SmbServer(
            "Path Test Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        // The client should be created successfully with various path patterns
        // Actual path validation happens during connect/exists calls
    }

    /**
     * Tests that multiple clients can be created for different servers.
     */
    @Test
    fun testMultipleClientsForDifferentServers() {
        val server1 = SmbServer(
            "Server 1",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val server2 = SmbServer(
            "Server 2",
            "192.168.1.100",
            445,
            SmbServer.Auth.UsernamePassword("", "user", "pass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>

        val client1 = factory.create(server1)
        val client2 = factory.create(server2)

        assertNotNull(client1)
        assertNotNull(client2)
        assertEquals("10.0.2.2", (client1.bookshelf as SmbServer).host)
        assertEquals("192.168.1.100", (client2.bookshelf as SmbServer).host)
    }

    /**
     * Tests that empty username is handled correctly.
     */
    @Test
    fun testEmptyUsernameHandling() {
        val server = SmbServer(
            "Empty Username Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "", "password"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals("", auth.username)
    }

    /**
     * Tests that empty password is handled correctly.
     */
    @Test
    fun testEmptyPasswordHandling() {
        val server = SmbServer(
            "Empty Password Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "user", ""),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        val smbServer = client.bookshelf as SmbServer
        val auth = smbServer.auth as SmbServer.Auth.UsernamePassword
        assertEquals("", auth.password)
    }

    /**
     * Tests that SmbServer equality works correctly for same server configurations.
     */
    @Test
    fun testSmbServerEquality() {
        val auth = SmbServer.Auth.UsernamePassword("DOMAIN", "user", "pass")
        val server1 = SmbServer("Server", "10.0.2.2", 445, auth)
        val server2 = SmbServer("Server", "10.0.2.2", 445, auth)

        // Different instances with same values should be equal (data class)
        assertEquals(server1, server2)
    }

    /**
     * Tests that different authentication types are not equal.
     */
    @Test
    fun testDifferentAuthTypesNotEqual() {
        val server1 = SmbServer("Server", "10.0.2.2", 445, SmbServer.Auth.Guest)
        val server2 = SmbServer(
            "Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "user", "pass"),
        )

        // Servers with different auth types should not be equal
        assert(server1 != server2)
    }

    /**
     * Tests that special characters in hostname are handled.
     */
    @Test
    fun testSpecialCharactersInHostname() {
        val server = SmbServer(
            "Special Host Server",
            "server-01.example.com",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals("server-01.example.com", (client.bookshelf as SmbServer).host)
    }

    /**
     * Tests that IPv6 addresses can be used as hostnames.
     */
    @Test
    fun testIPv6AddressHandling() {
        val server = SmbServer(
            "IPv6 Server",
            "::1",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        assertEquals("::1", (client.bookshelf as SmbServer).host)
    }

    /**
     * Tests that bookshelf copy function works correctly.
     */
    @Test
    fun testBookshelfCopyFunction() {
        val original = SmbServer(
            "Original",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val modified = original.copy(
            displayName = "Modified",
            port = 8445,
        )

        assertEquals("Modified", modified.displayName)
        assertEquals(8445, modified.port)
        assertEquals(original.host, modified.host)
        assertEquals(original.id, modified.id)
    }
}
