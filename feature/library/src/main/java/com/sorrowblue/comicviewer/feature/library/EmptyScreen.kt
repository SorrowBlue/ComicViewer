package com.sorrowblue.comicviewer.feature.library

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph

@Destination<ExternalModuleGraph>
@Composable
internal fun EmptyScreen(value: Int = 0) {
    Text(text = "$value")
}
