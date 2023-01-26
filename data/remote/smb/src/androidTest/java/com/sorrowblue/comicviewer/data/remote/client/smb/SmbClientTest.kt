package com.sorrowblue.comicviewer.data.remote.client.smb

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SmbServerModel
import com.sorrowblue.comicviewer.data.remote.client.FileClientException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class SmbClientTest {

    @Before
    fun before() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    private val nasServer = SmbServerModel(
        ServerModelId(0),
        "Test",
        "192.168.0.101",
        445,
        SmbServerModel.UsernamePassword("", "SorrowBlue", "nasyuukiasuna2s2")
    )

    @Test
    fun connectTest() {
        runTest {
            val result = SmbFileClient(nasServer).connect("/test/")
            Assert.assertEquals(result, Unit)
        }
    }

    @Test
    fun connectGuestTest() {
        runTest {
            val result = SmbFileClient(nasServer.copy(auth = SmbServerModel.Guest)).connect("/test/")
            Assert.assertEquals(result, Unit)
        }
    }

    /**
     * jcifs.smb.SmbException: Failed to connect: 0.0.0.0<00>/192.168.0.101
     * jcifs.util.transport.TransportException: java.net.ConnectException: failed to connect to /192.168.0.101 (port 445) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)
     * java.net.ConnectException: failed to connect to /192.168.0.101 (port 445) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)
     * android.system.ErrnoException: connect failed: ENETUNREACH (Network is unreachable)
     * ntStatus=NT_STATUS_UNSUCCESSFUL
     */
    @Ignore("ネットワークを無効にして実行")
    @Test
    fun connectTest_no_network() {
        Assert.assertThrows(FileClientException.NoNetwork::class.java) {
            runTest {
                SmbFileClient(nasServer).connect("/test/")
            }
        }
    }

    @Test
    fun connectTest_bad_path() = runTest {
        Assert.assertThrows(FileClientException.InvalidPath::class.java) {
            runTest {
                SmbFileClient(nasServer).connect("/test/a")
            }
        }
    }

    /**
     * jcifs.smb.SmbException: The network name cannot be found.
     * ntStatus=NT_STATUS_BAD_NETWORK_NAME
     */
    @Test
    fun connectTest_bad_share_name() {
        Assert.assertThrows(FileClientException.InvalidPath::class.java) {
            runTest {
                SmbFileClient(nasServer).connect("/test2/")
            }
        }
    }

    /**
     * jcifs.smb.SmbException: Failed to connect: 0.0.0.0<00>/192.168.0.100
     * jcifs.util.transport.ConnectionTimeoutException: Connection timeout
     * ntStatus=NT_STATUS_UNSUCCESSFUL
     */
    @Test
    fun connectTest_bad_ip() {
        Assert.assertThrows(FileClientException.InvalidServer::class.java) {
            runTest {
                SmbFileClient(nasServer.copy(host = "192.168.0.100")).connect("/test/")
            }
        }
    }

    /**
     * jcifs.util.transport.ConnectionTimeoutException: Connection timeout
     * ntStatus=NT_STATUS_UNSUCCESSFUL
     */
    @Test
    fun connectTest_bad_port() {
        Assert.assertThrows(FileClientException.InvalidServer::class.java) {
            runTest {
                SmbFileClient(nasServer.copy(port = 1)).connect("/test/")
            }
        }
    }

    /**
     * jcifs.smb.SmbAuthException: Logon failure: unknown user name or bad password.
     * ntStatus=NT_STATUS_LOGON_FAILURE
     */
    @Test
    fun connectTest_bad_auth() {
        Assert.assertThrows(FileClientException.InvalidAuth::class.java) {
            runTest {
                SmbFileClient(nasServer.copy(auth = SmbServerModel.Guest)).connect("/test/")
            }
        }
    }
}
