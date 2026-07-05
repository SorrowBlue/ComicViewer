package com.sorrowblue.comicviewer.framework.permission.localnetwork

import android.Manifest.permission.ACCESS_LOCAL_NETWORK
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import logcat.logcat

@Composable
actual fun rememberLocalNetworkPermissionRequester(
    initCheck: Boolean,
): LocalNetworkPermissionRequester {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val state = remember(activity) {
        AndroidLocalNetworkPermissionRequester(initCheck, activity)
    }
    state.settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = state::onActivityResult,
    )
    state.permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = state::onPermissionResult,
    )
    return state
}

private class AndroidLocalNetworkPermissionRequester(
    initCheck: Boolean,
    private val activity: Activity?,
) : LocalNetworkPermissionRequester {

    lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    lateinit var settingsLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

    fun onActivityResult(result: ActivityResult) {
        logcat { "#onActivityResult $result" }
        checkPermission()
    }

    override var state by mutableStateOf<LocalNetworkPermissionState>(
        LocalNetworkPermissionState.Pending,
    )

    fun onPermissionResult(granted: Boolean) {
        state = when {
            granted -> LocalNetworkPermissionState.Granted

            activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                ACCESS_LOCAL_NETWORK,
            ) -> LocalNetworkPermissionState.Rationale

            else -> LocalNetworkPermissionState.DeniedPermanent
        }
    }

    override fun onPermissionConfirmClick() {
        if (state is LocalNetworkPermissionState.Rationale) {
            launchPermissionRequest()
        } else if (state is LocalNetworkPermissionState.DeniedPermanent) {
            openPermissionSettings()
        }
    }

    override fun reset() {
        state = LocalNetworkPermissionState.Pending
    }

    override fun checkPermission(): Boolean {
        state = if (activity == null) {
            logcat { "#initialize activity is null" }
            LocalNetworkPermissionState.DeniedPermanent
        } else if (Build.VERSION.SDK_INT >= 37) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    ACCESS_LOCAL_NETWORK,
                ) == PackageManager.PERMISSION_GRANTED -> LocalNetworkPermissionState.Granted

                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    ACCESS_LOCAL_NETWORK,
                ) -> LocalNetworkPermissionState.Rationale

                else -> {
                    val pref =
                        activity.getSharedPreferences(
                            "ACCESS_LOCAL_NETWORK",
                            Context.MODE_PRIVATE,
                        )
                    val permissionRequested = pref.getBoolean("KEY_PERMISSION_REQUESTED", false)
                    if (permissionRequested) {
                        LocalNetworkPermissionState.DeniedPermanent
                    } else {
                        LocalNetworkPermissionState.Rationale
                    }
                }
            }
        } else {
            LocalNetworkPermissionState.Granted
        }
        logcat { "#initialize state=$state" }
        return state is LocalNetworkPermissionState.Granted
    }

    init {
        if (initCheck) {
            checkPermission()
        }
    }

    private fun launchPermissionRequest() {
        if (Build.VERSION.SDK_INT >= 37) {
            activity?.getSharedPreferences("ACCESS_LOCAL_NETWORK", Context.MODE_PRIVATE)?.edit {
                putBoolean("KEY_PERMISSION_REQUESTED", true)
            }

            permissionLauncher.launch(ACCESS_LOCAL_NETWORK)
        }
    }

    private fun openPermissionSettings() {
        val activity = activity ?: return
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        settingsLauncher.launch(intent)
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
