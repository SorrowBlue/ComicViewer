package com.sorrowblue.comicviewer.feature.library.googledrive.data

import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.services.drive.model.FileList
import com.google.api.services.people.v1.model.Person
import java.io.OutputStream
import kotlinx.coroutines.flow.StateFlow

interface GoogleDriveApiRepository {

    val profile: StateFlow<Person?>

    suspend fun fetchProfile()

    suspend fun fileList(
        parent: String = "root",
        loadSize: Int = 10,
        pageToken: String? = null,
    ): FileList?

    suspend fun download(
        fileId: String,
        output: OutputStream,
        progressChanged: (MediaHttpDownloader) -> Unit,
    )

    suspend fun fileName(fileId: String): String?
}
