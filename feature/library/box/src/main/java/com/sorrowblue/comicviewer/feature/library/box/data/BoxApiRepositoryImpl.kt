package com.sorrowblue.comicviewer.feature.library.box.data

import androidx.datastore.core.DataStore
import com.box.sdk.BoxAPIConnection
import com.box.sdk.BoxAPIConnectionListener
import com.box.sdk.BoxAPIException
import com.box.sdk.BoxAPIResponseException
import com.box.sdk.BoxFile
import com.box.sdk.BoxFolder
import com.box.sdk.BoxItem
import com.box.sdk.BoxUser
import java.io.OutputStream
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat

private const val CLIENT_ID = "nihdm7dthg9lm7m3b41bpw7jp7b0lb9z"
private const val CLIENT_SECRET = "znx5P0kuwJ5LNqF3UG8Yw8Xs05dw4zNq"

internal class BoxApiRepositoryImpl(
    private val dropboxCredentialDataStore: DataStore<BoxConnectionState>,
) : BoxApiRepository {

    private val api = runBlocking { dropboxCredentialDataStore.data.first() }.let {
        if (it.state != null) {
            BoxAPIConnection.restore(CLIENT_ID, CLIENT_SECRET, it.state)
        } else {
            BoxAPIConnection(CLIENT_ID, CLIENT_SECRET)
        }
    }

    init {
        api.maxRetryAttempts = 2
        api.addListener(object : BoxAPIConnectionListener {
            override fun onRefresh(api: BoxAPIConnection) {
                logcat { "onRefresh $api" }
                @OptIn(DelicateCoroutinesApi::class)
                GlobalScope.launch {
                    dropboxCredentialDataStore.updateData { it.copy(state = api.save()) }
                }
            }

            override fun onError(api: BoxAPIConnection?, error: BoxAPIException?) {
                logcat { "onRefresh $api" }
                logcat { error?.asLog().orEmpty() }
                @OptIn(DelicateCoroutinesApi::class)
                GlobalScope.launch {
                    dropboxCredentialDataStore.updateData { it.copy(state = null) }
                }
            }
        })
    }

    override suspend fun authenticate(state: String, code: String, onSuccess: () -> Unit) {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                api.authenticate(code)
            }
        }.onSuccess {
            logcat { "認証成功。${api.accessToken},${api.save()}" }
            withContext(Dispatchers.IO) {
                dropboxCredentialDataStore.updateData { it.copy(state = api.save()) }
            }
            onSuccess()
        }.onFailure {
            logcat { "認証失敗" }
            dropboxCredentialDataStore.updateData { it.copy(state = null) }
        }
    }

    override val userInfoFlow = dropboxCredentialDataStore.data.map {
        if (it.state != null) {
            try {
                BoxUser.getCurrentUser(api).getInfo("id", "avatar_url", "name")
            } catch (e: BoxAPIResponseException) {
                if (e.responseCode == 401) {
                    logcat { "トークン切れ" }
                    dropboxCredentialDataStore.updateData { it.copy(state = null) }
                } else {
                    logcat { "エラー" + e.asLog() }
                }
                null
            }
        } else {
            null
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun currentUser(): BoxUser? {
        return kotlin.runCatching { BoxUser.getCurrentUser(api) }.getOrNull()
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return dropboxCredentialDataStore.data.map { it.state != null }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            try {
                api.revokeToken()
                dropboxCredentialDataStore.updateData { it.copy(state = null) }
            } catch (e: BoxAPIResponseException) {
                logcat { e.asLog() }
                dropboxCredentialDataStore.updateData { it.copy(state = null) }
            }
        }
    }

    override suspend fun list(path: String, limit: Long, offset: Long): List<BoxItem.Info> {
        val folder = try {
            if (path.isEmpty()) {
                BoxFolder.getRootFolder(api)
            } else {
                BoxFolder(api, path)
            }
        } catch (e: Exception) {
            logcat { e.asLog() }
            logcat { "トークン切れ" }
            dropboxCredentialDataStore.updateData { it.copy(state = null) }
            return emptyList()
        }
        return folder.toList()
    }

    override suspend fun fileThumbnail(id: String): String? {
        return withContext(Dispatchers.IO) {
            BoxFile(
                api,
                id
            ).getInfoWithRepresentations("[jpg?dimensions=32x32]").representations.firstOrNull()?.content?.urlTemplate?.replace(
                "{+asset_path}",
                ""
            )
        }
    }

    override suspend fun accessToken(): String {
        return withContext(Dispatchers.IO) {
            api.accessToken.orEmpty()
        }
    }

    override suspend fun download(
        path: String,
        outputStream: OutputStream,
        progress: (Double) -> Unit,
    ) {
        BoxFile(api, path).download(outputStream) { numBytes, totalBytes ->
            progress.invoke(numBytes.toDouble() / totalBytes)
        }
    }
}
