package com.sorrowblue.comicviewer.feature.library.googledrive.data

import android.content.Context
import com.google.android.gms.common.api.Scope
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import com.google.api.services.people.v1.PeopleService
import com.google.api.services.people.v1.model.Person
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.koin.dsl.module

internal val googleDriveModule = module {
    single<GoogleDriveApiRepository> { GoogleDriveApiRepositoryImpl(get(), get()) }
}

internal class GoogleDriveApiRepositoryImpl(
    private val context: Context,
    private val authRepository: GoogleAuthorizationRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GoogleDriveApiRepository {

    private val scopes = listOf(Scope(DriveScopes.DRIVE_READONLY))

    private fun driveService(credential: Credential) =
        Drive.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName(context.getString(com.sorrowblue.comicviewer.framework.ui.R.string.app_name))
            .build()

    override val profile = MutableStateFlow<Person?>(null)

    override suspend fun fileList(parent: String, loadSize: Int, pageToken: String?): FileList? {
        return authRepository.request(
            scopes = scopes,
            authenticated = {
                driveService(it).files().list()
                    .setQ(
                        "'$parent' in parents and trashed = false and (mimeType = 'application/vnd.google-apps.folder' or mimeType contains 'zip' or mimeType contains 'pdf')"
                    )
                    .setOrderBy("folder,name")
                    .setSpaces("drive")
                    .setPageSize(loadSize)
                    .setPageToken(pageToken)
                    .setFields("nextPageToken,files(id,name,parents,modifiedTime,size,mimeType,iconLink)")
                    .execute()
            },
            unauthorized = {
                null
            }
        )
    }

    override suspend fun fileName(fileId: String): String? {
        return authRepository.request(
            scopes,
            authenticated = { driveService(it).files().get(fileId).execute().name },
            unauthorized = { null }
        )
    }

    override suspend fun download(
        fileId: String,
        output: OutputStream,
        progressChanged: (MediaHttpDownloader) -> Unit,
    ) {
        authRepository.request(
            scopes,
            authenticated = {
                driveService(it).files().get(fileId).apply {
                    mediaHttpDownloader.isDirectDownloadEnabled = false
                    mediaHttpDownloader.chunkSize = MediaHttpDownloader.MAXIMUM_CHUNK_SIZE
                    mediaHttpDownloader.setProgressListener(progressChanged)
                    executeMediaAndDownloadTo(output)
                }
            },
            unauthorized = {}
        )
    }

    override suspend fun fetchProfile() {
        authRepository.request(
            scopes = scopes,
            authenticated = {
                withContext(dispatcher) {
                    PeopleService.Builder(
                        NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        it
                    ).build()
                        .people()
                        .get("people/me")
                        .setPersonFields("names,photos")
                        .execute()
                }
            },
            unauthorized = {
                null
            }
        ).let {
            profile.value = it
        }
    }
}
