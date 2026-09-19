/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler

@OptIn(ExperimentalCoilApi::class)
internal expect val provideAsyncImagePreviewHandler: ProvidedValue<AsyncImagePreviewHandler>
    @Composable
    get
