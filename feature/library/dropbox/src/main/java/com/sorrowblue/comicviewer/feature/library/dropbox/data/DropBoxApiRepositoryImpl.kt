package com.sorrowblue.comicviewer.feature.library.dropbox.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.InvalidAccessTokenException
import com.dropbox.core.android.Auth
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ListFolderResult
import com.dropbox.core.v2.users.FullAccount
import com.sorrowblue.comicviewer.app.IoDispatcher
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
private val Context.dropboxCredentialDataStore: DataStore<DropboxCredential> by dataStore(
    fileName = "dropbox_credential.pb",
    serializer = DropboxCredential.Serializer()
)

val dropBoxModule = module {
    single<DropBoxApiRepository> { DropBoxApiRepositoryImpl(get(), get(named<IoDispatcher>())) }
}

internal class DropBoxApiRepositoryImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DropBoxApiRepository {

    private val dropboxCredentialDataStore = context.dropboxCredentialDataStore

    private val config = DbxRequestConfig
        .newBuilder("ComicViewerAndroid/${context.getPackageInfo().longVersionCode}")
        .withAutoRetryEnabled()
        .build()

    private suspend fun client(): DbxClientV2 {
        return DbxClientV2(config, readCredential())
    }

    override val isAuthenticated = dropboxCredentialDataStore.data.map { credential ->
        credential.credential != null && kotlin.runCatching {
            client().check().user("auth_check").result == "auth_check"
        }.onFailure {
            logcat { it.asLog() }
            dropboxCredentialDataStore.updateData { dropboxCredential ->
                dropboxCredential.copy(
                    credential = null
                )
            }
        }.getOrDefault(false)
    }.flowOn(dispatcher)

    override suspend fun storeCredential(dbxCredential: DbxCredential) {
        withContext(dispatcher) {
            logcat { "storeCredential(dbxCredential=$dbxCredential)" }
            dropboxCredentialDataStore.updateData {
                it.copy(credential = DbxCredential.Writer.writeToString(dbxCredential))
            }
        }
    }

    override val accountFlow = dropboxCredentialDataStore.data.map { credential ->
        if (credential.credential != null) {
            kotlin.runCatching { client().users().currentAccount }.onFailure {
                it.printStackTrace()
            }.getOrNull()
        } else {
            null
        }
    }.flowOn(dispatcher)

    override fun startSignIn() {
        val info = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val key = info.metaData.getString("dropboxApiKey")
        Auth.startOAuth2PKCE(
            context,
            key,
            requestConfig = config
        )
    }

    override suspend fun dbxCredential(): Boolean {
        Auth.getDbxCredential()?.let {
            logcat { "dropbox 認証した" }
            storeCredential(it)
            return true
        } ?: kotlin.run {
            logcat { "dropbox 認証してない" }
            return false
        }
    }

    override suspend fun currentAccount(): FullAccount? {
        return if (isAuthenticated.first()) {
            kotlin.runCatching { client().users().currentAccount }.onFailure {
                it.printStackTrace()
            }.getOrNull()
        } else {
            null
        }
    }

    override suspend fun signOut() {
        withContext(dispatcher) {
            client().auth().tokenRevoke()
            dropboxCredentialDataStore.updateData { it.copy(credential = null) }
        }
    }

    override suspend fun list(path: String, limit: Long, cursor: String?): ListFolderResult? {
        return if (isAuthenticated.first()) {
            kotlin.runCatching {
                if (cursor != null) {
                    client().files().listFolderContinue(cursor)
                } else {
                    client().files().listFolderBuilder(path).withLimit(limit).start()
                }
            }.onFailure {
                if (it is InvalidAccessTokenException) {
                    if (it.authError.isExpiredAccessToken) {
                        refresh()
                    }
                }
                it.printStackTrace()
            }.getOrNull()
        } else {
            null
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val credential = readCredential()
            credential?.refresh(config)
                ?.also {
                    storeCredential(credential)
                }
        }
    }

    override suspend fun download(
        path: String,
        outputStream: OutputStream,
        progress: suspend (Double) -> Unit,
    ) {
        val dbxDownloader = client().files().download(path)
        val size = dbxDownloader.result.size.toDouble()
        dbxDownloader.download(outputStream) {
            runBlocking {
                progress(it / size)
            }
        }
    }

    override suspend fun downloadLink(path: String): String {
        return client().files().getTemporaryLink(path).link
    }

    private suspend fun readCredential(): DbxCredential? {
        return try {
            DbxCredential.Reader.readFully(dropboxCredentialDataStore.data.first().credential)
        } catch (e: Exception) {
            logcat(priority = LogPriority.ERROR) { e.asLog() }
            dropboxCredentialDataStore.updateData { it.copy(credential = null) }
            null
        }
    }

    private fun Context.getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }
}
