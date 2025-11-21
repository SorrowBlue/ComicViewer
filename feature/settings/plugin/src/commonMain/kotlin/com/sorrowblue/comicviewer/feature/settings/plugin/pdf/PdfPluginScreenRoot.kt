package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

import androidx.compose.runtime.Composable

@Composable
context(context: PdfPluginScreenContext)
internal expect fun PdfPluginScreenRoot(onBackClick: () -> Unit)
