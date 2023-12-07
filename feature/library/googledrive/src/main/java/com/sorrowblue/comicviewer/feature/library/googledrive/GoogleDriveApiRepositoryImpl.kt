package com.sorrowblue.comicviewer.feature.library.googledrive

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import logcat.logcat
import org.koin.dsl.module

val googleDriveModule = module {
    single<GoogleDriveApiRepository> { GoogleDriveApiRepositoryImpl(get()) }
}

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

internal class GoogleDriveApiRepositoryImpl(private val context: Context) :
    GoogleDriveApiRepository {

    private val credential =
        GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_READONLY))

    override val googleSignInAccount =
        MutableStateFlow(GoogleSignIn.getLastSignedInAccount(context))

    override val driverServiceFlow = googleSignInAccount.filterNotNull().map {
        credential.selectedAccount = it.account
        Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName(context.getString(com.sorrowblue.comicviewer.framework.ui.R.string.app_name))
            .build()
    }

    override fun updateAccount() {
        googleSignInAccount.value = GoogleSignIn.getLastSignedInAccount(context)
    }

    override fun logout(activity: Activity, complete: () -> Unit) {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_READONLY))
                .build()
        GoogleSignIn.getClient(activity, googleSignInOptions).signOut().addOnCompleteListener {
            complete()
        }
    }

    override fun signInResult(result: ActivityResult, success: () -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            runCatching {
                GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                success()
            }.onFailure {
                it.printStackTrace()
                if (it is ApiException) {
                    logcat("APP") { "認証に失敗しました。(${it.statusCode})" }
                } else if (it is RuntimeExecutionException && it.cause is ApiException) {
                    logcat("APP") { "認証に失敗しました。(${(it.cause as ApiException).statusCode})" }
                } else {
                    logcat("APP") { "エラーが発生しました。" }
                }
            }
        } else {
            logcat("APP") { "キャンセルしました。" }
        }
    }

    override fun startSignIn(
        activity: Activity,
        activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    ) {
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_READONLY))
                .build()
        activityResultLauncher.launch(
            GoogleSignIn.getClient(
                activity,
                googleSignInOptions
            ).signInIntent
        )
    }
}
