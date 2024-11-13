package com.sorrowblue.comicviewer.feature.library.onedrive.data

import android.app.Activity
import com.microsoft.graph.models.DriveItemCollectionResponse
import com.microsoft.graph.models.User
import com.microsoft.graph.serviceclient.GraphServiceClient
import com.sorrowblue.comicviewer.app.IoDispatcher
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

    private val graphClient: GraphServiceClient = GraphServiceClient(
        authenticationProvider,
        "https://graph.microsoft.com/.default",
    )

    override suspend fun initialize() = authenticationProvider.initialize()

    override val accountFlow = authenticationProvider.account
    override val authStatus = authenticationProvider.authStatus

    override suspend fun getCurrentUser(): User? {
        return withContext(dispatcher) {
            if (!authenticationProvider.isSignIned) {
                null
            } else {
                graphClient.me().get()
            }
        }
    }

    override suspend fun profileImage(): InputStream {
        return withContext(dispatcher) {
            graphClient.me().photo().content().get()!!
        }
    }

    override suspend fun download(
        itemId: String,
        outputStream: OutputStream,
        onProgress: (Double) -> Unit,
    ) {
        withContext(dispatcher) {
            val driveId = graphClient.me().drive().get().id
            val size =
                graphClient.drives().byDriveId(driveId).items().byDriveItemId(itemId)
                    .get().size.toDouble()
            graphClient.drives().byDriveId(driveId).items().byDriveItemId(itemId).content().get()
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
        itemId: String,
        limit: Int,
        skipToken: String?,
    ): DriveItemCollectionResponse {
        logcat { "list(itemId=$itemId, limit=$limit, skipToken=$skipToken)" }
        val driveItemId = itemId.ifEmpty { "root" }
        val driveId = graphClient.me().drive().get().id
        val children = graphClient.drives().byDriveId(driveId).items().byDriveItemId(driveItemId)
            .children().run {
                if (skipToken != null) {
                    withUrl(skipToken)
                } else {
                    this
                }
            }
        return withContext(dispatcher) {
            runCatching {
                children.get {
                    it.queryParameters.apply {
                        top = limit
                        expand = arrayOf("thumbnails")
                    }
                }
            }.getOrElse { DriveItemCollectionResponse() }
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
