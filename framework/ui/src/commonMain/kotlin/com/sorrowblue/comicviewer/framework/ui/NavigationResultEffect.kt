package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultConsumer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.getResultState
import kotlinx.serialization.json.Json

@Composable
fun <T> NavigationResultEffect(key: SerializableNavigationResultKey<T>, onResult: (T) -> Unit) {
    val resultConsumer = LocalNavigationResultConsumer.current
    val navigationResult by remember(resultConsumer) {
        resultConsumer.getResultState(Json, key)
    }
    LaunchedEffect(navigationResult) {
        val result = navigationResult
        if (result != null) {
            val screenResult = result.getResult()
            onResult(screenResult)
            resultConsumer.clearResult(result.resultKey)
        }
    }
}
