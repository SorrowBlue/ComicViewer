package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

@Composable
inline fun <reified R : @Serializable Any> NavController.navResultSender(screen: KClass<out Any>): NavResultSender<R> =
    navResultSender(screen, kSerializerType<R>(), this)

@PublishedApi
@Composable
internal fun <R : @Serializable Any> navResultSender(
    currentNavScreen: KClass<out Any>,
    serializerType: KSerializerType<R>,
    navController: NavController,
): NavResultSender<R> {
    val sender = remember(navController, currentNavScreen, serializerType) {
        NavResultSenderImpl(navController, currentNavScreen, serializerType)
    }
    sender.HandleCanceled()
    return sender
}

interface NavResultSender<R : @Serializable Any> {
    fun setResult(result: R)
    fun navigateBack()
    fun navigateBack(result: R)
}

private class NavResultSenderImpl<R : @Serializable Any>(
    private val navController: NavController,
    currentNavScreen: KClass<out Any>,
    private val serializerType: KSerializerType<R>,
) : NavResultSender<R> {

    private val resultKey = resultKey(currentNavScreen, serializerType)
    private val canceledKey = cancelKey(currentNavScreen, serializerType)

    override fun navigateBack(result: R) {
        setResult(result)
        navigateBack()
    }

    override fun setResult(result: R) {
        navController.previousBackStackEntry?.savedStateHandle?.let {
            it[canceledKey] = false
            it[resultKey] = serializerType.toByteArray(result)
        }
    }

    override fun navigateBack() {
        navController.navigateUp()
    }

    @Composable
    fun HandleCanceled() {
        LifecycleResumeEffect(Unit) {
            val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
                ?: return@LifecycleResumeEffect onPauseOrDispose { }
            if (!savedStateHandle.contains(canceledKey)) {
                savedStateHandle[canceledKey] = true
            }
            onPauseOrDispose { }
        }
    }
}

internal fun resultKey(
    currentNavScreen: KClass<out Any>,
    navResultType: KSerializerType<out Any>,
): String {
    return "nav-result-value@${currentNavScreen.qualifiedName}@${navResultType::class.qualifiedName}"
}

internal fun cancelKey(
    currentNavScreen: KClass<out Any>,
    navResultType: KSerializerType<out Any>,
): String {
    return "nav-result-cancel@${currentNavScreen.qualifiedName}@${navResultType::class.qualifiedName}"
}
