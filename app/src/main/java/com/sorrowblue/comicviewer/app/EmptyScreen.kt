package com.sorrowblue.comicviewer.app

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination

@Destination<MainGraph>
@Composable
internal fun EmptyScreen(value: Int = 0) {
    Text(text = "$value")
}
