/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.framework.common.appGraph
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.manualFileKitCoreInitialization
import java.net.DatagramSocket
import java.net.InetAddress

private const val NETWORK_WAIT_TIMEOUT_MS = 30_000L
private const val NETWORK_POLL_INTERVAL_MS = 250L
private const val DEFAULT_SMB_PORT = 445

actual fun setupTest() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    ParcelFileDescriptor.AutoCloseInputStream(
        instrumentation.uiAutomation.executeShellCommand(
            "pm grant ${instrumentation.targetContext.packageName} ${Manifest.permission.ACCESS_LOCAL_NETWORK}",
        ),
    ).use { it.readBytes() }
    waitForNetworkAvailable(instrumentation.targetContext)
    FileKit.manualFileKitCoreInitialization(instrumentation.context)
}

private fun waitForNetworkAvailable(context: Context) {
    val connectivityManager = context.getSystemService(ConnectivityManager::class.java) ?: return
    val targetHost = BuildConfig.SMB_HOST.ifEmpty { "10.0.2.2" }
    val targetPort = if (BuildConfig.SMB_PORT > 0) BuildConfig.SMB_PORT else DEFAULT_SMB_PORT
    val deadline = System.currentTimeMillis() + NETWORK_WAIT_TIMEOUT_MS
    while (System.currentTimeMillis() < deadline) {
        val network = connectivityManager.activeNetwork
        val capabilities = network?.let(connectivityManager::getNetworkCapabilities)
        val linkProperties = network?.let(connectivityManager::getLinkProperties)
        val hasNetworkRoute =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                linkProperties?.routes?.isNotEmpty() == true
        if (hasNetworkRoute && canRouteToHost(targetHost, targetPort)) {
            return
        }
        Thread.sleep(NETWORK_POLL_INTERVAL_MS)
    }
}

private fun canRouteToHost(host: String, port: Int): Boolean =
    runCatching {
        DatagramSocket().use { socket ->
            socket.connect(InetAddress.getByName(host), port)
        }
    }.isSuccess

actual fun tearDownTest(appGraph: AppGraph) {
    // Do nothing
}

actual fun createAppGraph(): AppGraph {
    val context = InstrumentationRegistry.getInstrumentation().context
    return context.applicationContext.appGraph()
}

@Composable
actual fun AppContent(appGraph: AppGraph) {
    // Do nothing
}
