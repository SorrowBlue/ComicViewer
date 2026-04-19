package com.sorrowblue.comicviewer.feature.settings.extension.subscreen.pdf

import androidx.compose.runtime.Composable

@Composable
context(context: PdfPluginScreenContext)
internal expect fun PdfPluginScreenRoot(onBackClick: () -> Unit)
