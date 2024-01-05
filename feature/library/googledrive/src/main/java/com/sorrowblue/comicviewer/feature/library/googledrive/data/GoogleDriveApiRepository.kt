package com.sorrowblue.comicviewer.feature.library.googledrive.data

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.drive.Drive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface GoogleDriveApiRepository {
    val googleSignInAccount: MutableStateFlow<GoogleSignInAccount?>
    val driverServiceFlow: Flow<Drive>
    fun updateAccount()
    fun startSignIn(
        activity: Activity,
        activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    )

    fun logout(activity: Activity, complete: () -> Unit)
    fun signInResult(result: ActivityResult, success: () -> Unit)
}
