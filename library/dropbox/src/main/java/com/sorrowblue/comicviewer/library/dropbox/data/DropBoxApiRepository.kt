package com.sorrowblue.comicviewer.library.dropbox.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.users.FullAccount
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
interface DropBoxApiRepository {

    fun isAuthenticated(): Flow<Boolean>
    suspend fun storeCredential(dbxCredential: DbxCredential)

    val accountFlow: Flow<FullAccount?>

    suspend fun currentAccount(): FullAccount?
    suspend fun signOut()
    suspend fun list(path: String, limit: Long, cursor: String?): ListFolderResult?
    suspend fun download(path: String, outputStream: OutputStream, progress: (Double) -> Unit)

    companion object {
        private val Context.dropboxCredentialDataStore: DataStore<DropboxCredential> by dataStore(
            fileName = "dropbox_credential.pb", serializer = DropboxCredential.Serializer()
        )

        private var instance: DropBoxApiRepository? = null

        fun getInstance(context: Context) = instance ?: DropBoxApiRepositoryImpl(
            context,
            context.dropboxCredentialDataStore
        ).also { instance = it }
    }
}
