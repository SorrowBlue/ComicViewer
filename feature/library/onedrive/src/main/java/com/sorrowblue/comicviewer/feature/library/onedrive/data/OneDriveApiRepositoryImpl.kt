package com.sorrowblue.comicviewer.feature.library.onedrive.data

import android.app.Activity
import com.microsoft.graph.models.User
import com.microsoft.graph.requests.DriveItemCollectionPage
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.IAccount
import com.sorrowblue.comicviewer.app.IoDispatcher
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import logcat.logcat
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val oneDriveModule = module {
    single { AuthenticationProvider(get(), get(named<IoDispatcher>())) }
    single<OneDriveApiRepository> { OneDriveApiRepositoryImpl(get(), get(named<IoDispatcher>())) }
}

internal class OneDriveApiRepositoryImpl(
    private val authenticationProvider: AuthenticationProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OneDriveApiRepository {

    private val graphClient = GraphServiceClient.builder()
        .authenticationProvider(authenticationProvider)
        .logger(LogcatLogger)
        .buildClient()

    override suspend fun initialize() = authenticationProvider.initialize()

    override val accountFlow: StateFlow<IAccount?> = authenticationProvider.account

    override suspend fun getCurrentUser(): User? {
        return withContext(dispatcher) {
            if (!authenticationProvider.isSignIned) {
                null
            } else {
                graphClient.me().buildRequest().get()
            }
        }
    }

    override suspend fun profileImage(): InputStream {
        return withContext(dispatcher) {
            graphClient.me().photo().content().buildRequest().get()!!
        }
    }

    override suspend fun driveId(): String {
        return withContext(dispatcher) {
            graphClient.me().drive().buildRequest().get()!!.id!!
        }
    }

    override suspend fun download(
        driveId: String,
        itemId: String,
        outputStream: OutputStream,
        onProgress: (Double) -> Unit,
    ) {
        withContext(dispatcher) {
            val size =
                graphClient.drives(driveId).items(itemId).buildRequest().get()!!.size!!.toDouble()
            graphClient.drives(driveId).items(itemId).content().buildRequest().get()!!
                .copyTo(
                    ProgressOutputStream(outputStream) {
                        onProgress.invoke(it / size)
                    }
                )
        }
    }

    override fun loadAccount() {
        authenticationProvider.loadAccount()
    }

    override suspend fun list(
        driveId: String?,
        itemId: String,
        limit: Int,
        skipToken: String?,
    ): DriveItemCollectionPage {
        return if (driveId == null) {
            withContext(dispatcher) {
                kotlin.runCatching {
                    graphClient.me().drive().root().children().buildRequest().apply {
                        top(limit)
                        expand("thumbnails")
                        skipToken?.let(::skipToken)
                    }.get()!!
                }.getOrElse {
                    throw it
//                    list(driveId, itemId, limit, skipToken)
                }
            }
        } else {
            withContext(dispatcher) {
                graphClient.drives(driveId).items(itemId).children().buildRequest().apply {
                    top(limit)
                    expand("thumbnails")
                    skipToken?.let(::skipToken)
                }.get()!!
            }
        }
    }

    override suspend fun login(activity: Activity) {
        kotlin.runCatching {
            authenticationProvider.signIn(activity).await()
        }.onSuccess {
            logcat { "success account.id=${it.account.id}" }
            logcat { "success account.idToken=${it.account.idToken}" }
        }.onFailure {
            it.printStackTrace()
        }
    }

    override suspend fun logout() {
        authenticationProvider.signOut()
    }
}
