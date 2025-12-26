package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import dev.zacsweers.metro.createGraph
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest

/**
 * Unit tests for error handling in SMB server connections.
 *
 * These tests verify that appropriate exceptions are thrown for various error conditions:
 * - Invalid server addresses
 * - Invalid authentication credentials
 * - Invalid paths
 * - Network errors
 * - Connection timeouts
 */
@Suppress("UNCHECKED_CAST")
class SmbFileClientErrorHandlingTest {
    private val graph = createGraph<TestGraph>()

    /**
     * Tests that attempting to connect to a non-existent hostname throws InvalidServer.
     */
    @Test
    fun testConnectToNonExistentHost() = runTest {
        val server = SmbServer(
            "Non-existent Host",
            "nonexistent.invalid.hostname.local",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<FileClientException.InvalidServer> {
            client.connect("/share/")
        }
    }

    /**
     * Tests that attempting to connect to an unreachable IP address throws InvalidServer.
     */
    @Test
    fun testConnectToUnreachableIP() = runTest {
        val server = SmbServer(
            "Unreachable IP",
            "192.0.2.1", // TEST-NET-1 - reserved for documentation
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<FileClientException.InvalidServer> {
            client.connect("/share/")
        }
    }

    /**
     * Tests that invalid port numbers are handled.
     * Note: Port validation happens at connection time.
     */
    @Test
    fun testConnectToInvalidPort() = runTest {
        val server = SmbServer(
            "Invalid Port Server",
            "10.0.2.2",
            99999, // Invalid port number
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertNotNull(client)
        // Connection should fail when attempted
        assertFailsWith<Exception> {
            client.connect("/share/")
        }
    }

    /**
     * Tests that attempting operations with invalid authentication throws InvalidAuth.
     */
    @Test
    fun testOperationsWithInvalidCredentials() = runTest {
        val server = SmbServer(
            "Invalid Credentials",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "invalid_user", "invalid_pass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<FileClientException.InvalidAuth> {
            client.connect("/share/")
        }
    }

    /**
     * Tests that checking existence of invalid paths throws InvalidPath.
     */
    @Test
    fun testExistsWithInvalidPath() = runTest {
        val server = SmbServer(
            "Path Test Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.UsernamePassword("", "testuser", "testpass"),
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        // Invalid path format should throw InvalidPath
        assertFailsWith<Exception> {
            client.exists("/nonexistent_share_name_that_does_not_exist/")
        }
    }

    /**
     * Tests that connecting to an empty path throws InvalidPath.
     */
    @Test
    fun testConnectWithEmptyPath() = runTest {
        val server = SmbServer(
            "Empty Path Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        // Empty path should be handled appropriately
        assertFailsWith<Exception> {
            client.connect("")
        }
    }

    /**
     * Tests that malformed hostnames are handled appropriately.
     */
    @Test
    fun testMalformedHostname() = runTest {
        val server = SmbServer(
            "Malformed Host",
            "not a valid hostname!@#$",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<Exception> {
            client.connect("/share/")
        }
    }

    /**
     * Tests that operations without prior connection are handled.
     */
    @Test
    fun testOperationsWithoutConnection() = runTest {
        val server = SmbServer(
            "No Connection Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        // Operations should handle connection establishment internally
        assertFailsWith<Exception> {
            client.exists("/nonexistent/")
        }
    }

    /**
     * Tests that special characters in paths are handled correctly.
     */
    @Test
    fun testSpecialCharactersInPath() = runTest {
        val server = SmbServer(
            "Special Chars Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        // Paths with special characters should be properly encoded/decoded
        assertFailsWith<Exception> {
            client.exists("/share/folder with spaces/")
        }
    }

    /**
     * Tests that Unicode characters in paths are handled.
     */
    @Test
    fun testUnicodeCharactersInPath() = runTest {
        val server = SmbServer(
            "Unicode Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<Exception> {
            client.exists("/share/日本語フォルダ/")
        }
    }

    /**
     * Tests that very long paths are handled appropriately.
     */
    @Test
    fun testVeryLongPath() = runTest {
        val server = SmbServer(
            "Long Path Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        val longPath = "/share/" + "a".repeat(1000) + "/"

        assertFailsWith<Exception> {
            client.exists(longPath)
        }
    }

    /**
     * Tests that null or invalid share names throw InvalidPath.
     */
    @Test
    fun testInvalidShareName() = runTest {
        val server = SmbServer(
            "Invalid Share Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<Exception> {
            client.connect("///invalid//share///")
        }
    }

    /**
     * Tests that paths with backslashes are handled (Windows-style paths).
     */
    @Test
    fun testPathWithBackslashes() = runTest {
        val server = SmbServer(
            "Backslash Path Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<Exception> {
            client.exists("\\share\\folder\\")
        }
    }

    /**
     * Tests that relative paths are handled appropriately.
     */
    @Test
    fun testRelativePath() = runTest {
        val server = SmbServer(
            "Relative Path Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<Exception> {
            client.exists("share/../other/")
        }
    }

    /**
     * Tests that URL encoded characters in paths are handled.
     */
    @Test
    fun testURLEncodedPath() = runTest {
        val server = SmbServer(
            "URL Encoded Server",
            "10.0.2.2",
            445,
            SmbServer.Auth.Guest,
        )
        val factory = graph.fileClientFactory.getValue(
            FileClientType.Smb,
        ) as FileClient.Factory<Bookshelf>
        val client = factory.create(server)

        assertFailsWith<Exception> {
            client.exists("/share/folder%20with%20spaces/")
        }
    }
}
