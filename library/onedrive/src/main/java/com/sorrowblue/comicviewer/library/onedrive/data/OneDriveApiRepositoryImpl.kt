package com.sorrowblue.comicviewer.library.onedrive.data

import com.microsoft.graph.logger.ILogger
import com.microsoft.graph.logger.LoggerLevel
import com.microsoft.graph.models.User
import com.microsoft.graph.requests.DriveItemCollectionPage
import com.microsoft.graph.requests.GraphServiceClient
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import logcat.logcat

internal class OneDriveApiRepositoryImpl(private val authenticationProvider: AuthenticationProvider) :
    OneDriveApiRepository {

    private val graphClient = GraphServiceClient.builder()
        .authenticationProvider(authenticationProvider)
        .logger(object : ILogger {
            override fun setLoggingLevel(level: LoggerLevel) {}

            override fun getLoggingLevel(): LoggerLevel {
                return LoggerLevel.DEBUG
            }

            override fun logDebug(message: String) {
                logcat { message }
            }

            override fun logError(message: String, throwable: Throwable?) {
                throwable?.printStackTrace()
                logcat { message + ". ${throwable?.localizedMessage}" }
            }

        })
        .buildClient()

    override suspend fun getCurrentUser(): User? {
        return withContext(Dispatchers.IO) {
            if (!authenticationProvider.isSingIned) {
                null
            } else {
                graphClient.me().buildRequest().get()
            }
        }
    }

    override suspend fun profileImage(): InputStream {
        return withContext(Dispatchers.IO) {
            graphClient.me().photo().content().buildRequest().get()!!
        }
    }

    override val currentUserFlow = authenticationProvider.currentAccountFlow.map {
        if (it != null) {
            getCurrentUser()
        } else {
            null
        }
    }

    override val isAuthenticated = authenticationProvider.isAuthenticated

    override suspend fun driveId(): String {
        return withContext(Dispatchers.IO) {
            graphClient.me().drive().buildRequest().get()!!.id!!
        }
    }

    override suspend fun download(
        driveId: String,
        itemId: String,
        outputStream: OutputStream,
        onProgress: (Double) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val size =
                graphClient.drives(driveId).items(itemId).buildRequest().get()!!.size!!.toDouble()
            graphClient.drives(driveId).items(itemId).content().buildRequest().get()!!
                .copyTo(ProgressOutputStream(outputStream) {
                    onProgress.invoke(it / size)
                })
        }
    }

    override suspend fun list(
        driveId: String?,
        itemId: String,
        limit: Int,
        skipToken: String?
    ): DriveItemCollectionPage {
        return if (driveId == null) {
            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    graphClient.me().drive().root().children().buildRequest().apply {
                        top(limit)
                        expand("thumbnails")
                        skipToken?.let(::skipToken)
                    }.get()!!
                }.getOrElse {
                    list(driveId, itemId, limit, skipToken)
                }
            }
        } else {
            withContext(Dispatchers.IO) {
                graphClient.drives(driveId).items(itemId).children().buildRequest().apply {
                    top(limit)
                    expand("thumbnails")
                    skipToken?.let(::skipToken)
                }.get()!!
            }
        }
    }
}
