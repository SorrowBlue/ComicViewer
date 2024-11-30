package com.sorrowblue.comicviewer.feature.library.box.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.box.sdk.BoxAPIConnection
import com.box.sdk.BoxAPIConnectionListener
import com.box.sdk.BoxAPIException
import com.box.sdk.BoxAPIResponseException
import com.box.sdk.BoxFile
import com.box.sdk.BoxFolder
import com.box.sdk.BoxItem
import com.box.sdk.BoxUser
import com.sorrowblue.comicviewer.app.IoDispatcher
import com.sorrowblue.comicviewer.feature.library.box.BuildConfig
import java.io.OutputStream
import java.net.URI
import kotlin.random.Random
import kotlinx.coroutines.CoroutineDispatcher
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
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.asLog
import logcat.logcat
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
private val Context.boxConnectionStateDataStore: DataStore<BoxConnectionState> by dataStore(
    fileName = "box_connection_state.pb",
    serializer = BoxConnectionState.Serializer()
)

internal val boxModule = module {

    single {
        get<Context>().boxConnectionStateDataStore.also {
            logcat { "boxConnectionStateDataStore=$it" }
        }
    }

    single<BoxApiRepository> { BoxApiRepositoryImpl(get(), get(named<IoDispatcher>())) }
}

internal class BoxApiRepositoryImpl(
    private val dropboxCredentialDataStore: DataStore<BoxConnectionState>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BoxApiRepository {

    private val api = runBlocking { dropboxCredentialDataStore.data.first() }.let {
        if (it.state != null) {
            BoxAPIConnection.restore(
                BuildConfig.BOX_CLIENT_ID,
                BuildConfig.BOX_CLIENT_SECRET,
                it.state
            )
        } else {
            BoxAPIConnection(BuildConfig.BOX_CLIENT_ID, BuildConfig.BOX_CLIENT_SECRET)
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

    override suspend fun authenticate(
        state: String,
        code: String,
        onSuccess: () -> Unit,
        fail: () -> Unit,
    ) {
        if (this.state != state) {
            logcat { "認証失敗" }
            dropboxCredentialDataStore.updateData { connectionState -> connectionState.copy(state = null) }
            fail()
            return
        }
        kotlin.runCatching {
            withContext(dispatcher) {
                api.authenticate(code)
            }
        }.onSuccess {
            logcat { "認証成功。${api.accessToken},${api.save()}" }
            withContext(dispatcher) {
                dropboxCredentialDataStore.updateData { it.copy(state = api.save()) }
            }
            onSuccess()
        }.onFailure {
            logcat { "認証失敗" }
            dropboxCredentialDataStore.updateData { connectionState -> connectionState.copy(state = null) }
            fail()
        }
    }

    override val userInfoFlow = dropboxCredentialDataStore.data.map { state ->
        if (state.state != null) {
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
    }.flowOn(dispatcher)

    override suspend fun currentUser(): BoxUser? {
        return kotlin.runCatching { BoxUser.getCurrentUser(api) }.getOrNull()
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return dropboxCredentialDataStore.data.map { it.state != null }
    }

    override suspend fun signOut() {
        withContext(dispatcher) {
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
                BoxFolder.getRootFolder(api).getChildrenRange(offset, limit, "name,size")
            } else {
                BoxFolder(api, path).getChildrenRange(offset, limit, "name,size")
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
        return withContext(dispatcher) {
            BoxFile(
                api,
                id
            ).getInfoWithRepresentations(
                "[jpg?dimensions=32x32]"
            ).representations.firstOrNull()?.content?.urlTemplate?.replace(
                "{+asset_path}",
                ""
            )
        }
    }

    override suspend fun accessToken(): String {
        return withContext(dispatcher) {
            api.accessToken.orEmpty()
        }
    }

    private var state: String? = null

    override fun getAuthorizationUrl(): String {
        state = Random.nextInt(20).toString()
        return BoxAPIConnection.getAuthorizationURL(
            BuildConfig.BOX_CLIENT_ID,
            URI.create("https://comicviewer.sorrowblue.com/box/oauth2"),
            state,
            null
        ).toString()
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
