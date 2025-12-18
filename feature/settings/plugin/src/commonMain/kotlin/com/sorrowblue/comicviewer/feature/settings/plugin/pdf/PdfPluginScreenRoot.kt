package com.sorrowblue.comicviewer.feature.settings.plugin.pdf

import androidx.compose.runtime.Composable

@Composable
context(context: PdfPluginScreenContext)
expect fun PdfPluginScreenRoot(onBackClick: () -> Unit)
