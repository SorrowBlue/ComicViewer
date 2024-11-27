package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.ramcosta.composedestinations.navargs.DestinationsNavType
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.scope.resultRecipient
import com.ramcosta.composedestinations.spec.DestinationSpec

val LocalDestinationScopeWithNoDependencies =
    compositionLocalOf<DestinationScope<*>?> { null }

val LocalSnackbarHostState = compositionLocalOf { SnackbarHostState() }

@Composable
inline fun <reified D : DestinationSpec, R> resultRecipient(
    resultNavType: DestinationsNavType<in R>,
): ResultRecipient<D, R> =
    with(LocalDestinationScopeWithNoDependencies.current!!) {
        this.resultRecipient(resultNavType)
    }
