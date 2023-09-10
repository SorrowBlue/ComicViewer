package com.sorrowblue.comicviewer.library.googledrive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

internal class GoogleDriveApiRepository(private val context: Context) {

    private val credential =
        GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_READONLY))

    val googleSignInAccount =
        MutableStateFlow(GoogleSignIn.getLastSignedInAccount(context))

    val driverServiceFlow = googleSignInAccount.filterNotNull().map {
        credential.selectedAccount = it.account
        Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("ComicViewer").build()
    }

    fun updateAccount() {
        googleSignInAccount.value = GoogleSignIn.getLastSignedInAccount(context)
    }
}
