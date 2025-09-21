package com.sorrowblue.comicviewer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer.Auth
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import logcat.logcat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class BookshelfInsertReceiver : BroadcastReceiver(), KoinComponent {

    private val registerBookshelfUseCase: RegisterBookshelfUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        logcat { "onReceive: ${intent.getStringExtra("json")}" }
        if (!intent.hasExtra("json")) return
        val jsonString = intent.getStringExtra("json") ?: return
        runCatching {
            Json.Default.decodeFromString<InsertJson>(jsonString)
        }.onSuccess { insertJson ->
            goAsync {
                insertJson.bookshelf.forEach {
                    registerBookshelfUseCase(
                        RegisterBookshelfUseCase.Request(
                            it.toModel(),
                            it.path
                        )
                    ).fold(
                        onSuccess = { bookshelf ->
                            Log.d(this::class.simpleName, "insert success. $bookshelf")
                        },
                        onError = { error ->
                            Log.e(this::class.simpleName, "insert error. $error")
                        }
                    )
                }
            }
        }.onFailure {
            Log.e(this::class.simpleName, "onReceive error: ${it.message}", it)
        }
    }
}

private fun BroadcastReceiver.goAsync(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit,
) {
    val parentScope = CoroutineScope(coroutineContext)
    val pendingResult = goAsync()

    parentScope.launch {
        try {
            try {
                // Use `coroutineScope` so that errors within `block` are rethrown at the end of
                // this scope, instead of propagating up the Job hierarchy. If we use `parentScope`
                // directly, then errors in child jobs `launch`ed by `block` would trigger the
                // CoroutineExceptionHandler and crash the process.
                coroutineScope { this.block() }
            } catch (_: CancellationException) {
                // Regular cancellation, do nothing. The scope will always be cancelled below.
            } catch (e: Throwable) {
                Log.e(this@goAsync::class.simpleName, "BroadcastReceiver execution failed", e)
            } finally {
                // Make sure the parent scope is cancelled in all cases. Nothing can be in the
                // `finally` block after this, as this throws a `CancellationException`.
                parentScope.cancel()
            }
        } finally {
            // Notify ActivityManager that we are finished with this broadcast. This must be the
            // last call, as the process may be killed after calling this.
            try {
                pendingResult.finish()
            } catch (e: IllegalStateException) {
                // On some OEM devices, this may throw an error about "Broadcast already finished".
                // See b/257513022.
                Log.e(
                    this@goAsync::class.simpleName,
                    "Error thrown when trying to finish broadcast",
                    e
                )
            }
        }
    }
}

@Serializable
internal data class InsertJson(
    val bookshelf: List<SmbServerJson>,
)

@Serializable
internal data class SmbServerJson(
    val displayName: String,
    val host: String,
    val port: Int,
    val username: String,
    val domain: String,
    val password: String,
    val path: String,
) {

    fun toModel() = SmbServer(
        displayName = displayName,
        host = host,
        port = port,
        auth = Auth.UsernamePassword(domain, username, password)
    )
}
