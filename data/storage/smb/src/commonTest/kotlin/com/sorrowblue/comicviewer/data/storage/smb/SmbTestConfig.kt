package com.sorrowblue.comicviewer.data.storage.smb

/**
 * Test configuration for SMB server connections.
 *
 * This object provides environment-specific configuration for running tests
 * in different environments (GitHub Actions, local development, Android emulator).
 */
object SmbTestConfig {
    /**
     * Gets the SMB server hostname appropriate for the current test environment.
     *
     * - In GitHub Actions (CI): uses "localhost" or service container hostname
     * - In Android emulator: uses "10.0.2.2" (special alias for host machine)
     * - In local desktop tests: uses "localhost"
     *
     * The environment is detected using the GITHUB_ACTIONS environment variable.
     */
    val smbServerHost: String
        get() = if (isGitHubActions()) {
            // In GitHub Actions, use localhost to connect to service container
            "localhost"
        } else {
            // For local testing and Android emulator
            // Android emulator uses 10.0.2.2 to reach host machine
            "10.0.2.2"
        }

    /**
     * SMB server port (standard SMB port).
     */
    const val smbServerPort: Int = 445

    /**
     * Test username for SMB authentication.
     * This matches the configuration in .github/workflows/test.yml
     */
    const val testUsername: String = "testuser"

    /**
     * Test password for SMB authentication.
     * This matches the configuration in .github/workflows/test.yml
     */
    const val testPassword: String = "testpass"

    /**
     * Test share name.
     * This matches the configuration in .github/workflows/test.yml
     */
    const val testShareName: String = "testshare"

    /**
     * Checks if tests are running in GitHub Actions environment.
     */
    private fun isGitHubActions(): Boolean = System.getenv("GITHUB_ACTIONS") == "true"
}
