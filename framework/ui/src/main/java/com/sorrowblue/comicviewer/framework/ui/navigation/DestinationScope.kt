package com.sorrowblue.comicviewer.framework.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.ramcosta.composedestinations.navargs.DestinationsNavType
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.scope.resultRecipient
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.wrapper.DestinationWrapper

val LocalDestinationScope =
    compositionLocalOf<DestinationScope<*>?> { null }

@Composable
inline fun <reified D : DestinationSpec, R> resultRecipient(
    resultNavType: DestinationsNavType<in R>,
): ResultRecipient<D, R> =
    with(LocalDestinationScope.current!!) {
        this.resultRecipient(resultNavType)
    }

object DefaultDestinationScopeWrapper : DestinationScopeWrapper

interface DestinationScopeWrapper : DestinationWrapper {
    @Composable
    override fun <T> DestinationScope<T>.Wrap(
        @SuppressLint(
            "ComposableLambdaParameterNaming"
        ) screenContent: @Composable () -> Unit,
    ) {
        CompositionLocalProvider(LocalDestinationScope provides this) {
            screenContent()
        }
    }
}
