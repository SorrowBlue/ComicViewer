package com.sorrowblue.comicviewer.library.dropbox.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.datastore.core.DataStore
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.users.FullAccount
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class DropBoxApiRepositoryImpl(
    context: Context,
    private val dropboxCredentialDataStore: DataStore<DropboxCredential>
) : DropBoxApiRepository {

    private val config = DbxRequestConfig
        .newBuilder("ComicViewerAndroid/${context.getPackageInfo().longVersionCode}")
        .build()

    private suspend fun client(): DbxClientV2 {
        return DbxClientV2(config, readCredential())
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return dropboxCredentialDataStore.data.map { credential ->
            credential.credential != null && kotlin.runCatching {
                client().check().user("auth_check").result == "auth_check"
            }.onFailure {
                it.printStackTrace()
                dropboxCredentialDataStore.updateData { it.copy(credential = null) }
            }.getOrDefault(false)
        }
    }

    override suspend fun storeCredential(dbxCredential: DbxCredential) {
        withContext(Dispatchers.IO) {
            dropboxCredentialDataStore.updateData {
                it.copy(credential = DbxCredential.Writer.writeToString(dbxCredential))
            }
        }
    }

    override val accountFlow = dropboxCredentialDataStore.data.map {
        if (it.credential != null) {
            kotlin.runCatching { client().users().currentAccount }.onFailure {
                it.printStackTrace()
            }.getOrNull()
        } else {
            null
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun currentAccount(): FullAccount? {
        return if (isAuthenticated().first()) {
            kotlin.runCatching { client().users().currentAccount }.onFailure {
                it.printStackTrace()
            }.getOrNull()
        } else {
            null
        }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            client().auth().tokenRevoke()
            dropboxCredentialDataStore.updateData { it.copy(credential = null) }
        }
    }

    override suspend fun list(path: String, limit: Long, cursor: String?): ListFolderResult? {
        return if (isAuthenticated().first()) {
            kotlin.runCatching {
                if (cursor != null) {
                    client().files().listFolderContinue(cursor)
                } else {
                    client().files().listFolderBuilder(path).withLimit(limit).start()
                }
            }.onFailure {
                it.printStackTrace()
            }.getOrNull()
        } else {
            null
        }
    }

    override suspend fun download(
        path: String,
        outputStream: OutputStream,
        progress: (Double) -> Unit
    ) {
        val dbxDownloader = client().files().download(path)
        val size = dbxDownloader.result.size.toDouble()
        dbxDownloader.download(outputStream) {
            progress(it / size)
        }
    }

    private suspend fun readCredential(): DbxCredential? {
        return try {
            DbxCredential.Reader.readFully(dropboxCredentialDataStore.data.first().credential)
        } catch (e: Exception) {
            dropboxCredentialDataStore.updateData { it.copy(credential = null) }
            null
        }
    }

    private fun Context.getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
    }

}
