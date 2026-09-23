/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey

interface NavEntryDecoratorProvider {

    @Composable
    fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey>
}
