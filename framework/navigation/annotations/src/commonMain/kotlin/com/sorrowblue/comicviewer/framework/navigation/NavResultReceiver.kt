package com.sorrowblue.comicviewer.framework.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

sealed interface NavResult<out R> {
    data object Canceled : NavResult<Nothing>
    data class Value<R>(val value: R) : NavResult<R>
}

@Composable
inline fun <reified N : Any, reified R : @Serializable Any> NavBackStackEntry.navResultReceiver(): NavResultReceiver<N, R> =
    navResultReceiver(this, N::class, kSerializerType())

@PublishedApi
@Composable
internal fun <N : Any, R : @Serializable Any> navResultReceiver(
    backStackEntry: NavBackStackEntry,
    originalNavScreen: KClass<out Any>,
    kSerializerByteArray: KSerializerByteArray<R>,
): NavResultReceiver<N, R> = remember(backStackEntry, originalNavScreen, kSerializerByteArray) {
    NavResultReceiverImpl(backStackEntry, originalNavScreen, kSerializerByteArray)
}

interface NavResultReceiver<N, R : @Serializable Any> {
    @Suppress("ComposableNaming")
    @Composable
    fun onNavResult(listener: @DisallowComposableCalls (NavResult<R>) -> Unit)
}

private class NavResultReceiverImpl<N : Any, R : @Serializable Any>(
    private val backStackEntry: NavBackStackEntry,
    originalNavScreen: KClass<out Any>,
    private val kSerializerByteArray: KSerializerByteArray<R>,
) : NavResultReceiver<N, R> {
    private val resultKey = resultKey(originalNavScreen, kSerializerByteArray)
    private val canceledKey = cancelKey(originalNavScreen, kSerializerByteArray)

    @Composable
    override fun onNavResult(listener: (NavResult<R>) -> Unit) {
        val currentListener by rememberUpdatedState(listener)
        LifecycleEventEffect(
            Lifecycle.Event.ON_START,
            Lifecycle.Event.ON_RESUME,
            key = backStackEntry
        ) {
            handleResult(currentListener)
        }
        LifecycleResumeEffect(key1 = backStackEntry) {
            handleResult(currentListener)
            onPauseOrDispose { }
        }
    }

    private fun handleResult(listener: (NavResult<R>) -> Unit) {
        if (!hasAnyResult()) return

        val canceled = backStackEntry.savedStateHandle.remove<Boolean>(canceledKey)

        if (canceled == true) {
            listener(NavResult.Canceled)
        } else if (backStackEntry.savedStateHandle.contains(resultKey)) {
            val result = backStackEntry.savedStateHandle.get<ByteArray>(resultKey)?.let {
                kSerializerByteArray.fromByteArray(it)
            }!!
            backStackEntry.savedStateHandle.remove<Any?>(resultKey)
            listener(NavResult.Value(result))
        }
    }

    private fun hasAnyResult(): Boolean {
        return backStackEntry.savedStateHandle.contains(canceledKey) ||
            backStackEntry.savedStateHandle.contains(resultKey)
    }
}

@Composable
internal fun LifecycleEventEffect(
    vararg events: Lifecycle.Event,
    key: Any,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: () -> Unit,
) {
    require(!events.contains(Lifecycle.Event.ON_DESTROY)) {
        "LifecycleEventEffect cannot be used to " +
            "listen for Lifecycle.Event.ON_DESTROY, since Compose disposes of the " +
            "composition before ON_DESTROY observers are invoked."
    }
    val currentOnEvent by rememberUpdatedState(onEvent)
    DisposableEffect(key, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, e ->
            if (events.contains(e)) {
                currentOnEvent()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
