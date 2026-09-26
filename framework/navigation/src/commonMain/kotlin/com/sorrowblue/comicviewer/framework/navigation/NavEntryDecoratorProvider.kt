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

typealias AppContentDecoratorProviders = Set<AppContentDecoratorProvider>

interface AppContentDecoratorProvider {

    val order: Int

    @Composable
    fun Content(isInitialized: Boolean, onInitialize: () -> Unit, content: @Composable () -> Unit)
}

@Composable
fun List<AppContentDecoratorProvider>.NestedAppContents(
    isInitialized: Boolean,
    onInitialize: () -> Unit,
    index: Int = 0,
    content: @Composable () -> Unit,
) {
    if (index < this.size) {
        this[index].Content(
            isInitialized = isInitialized,
            onInitialize = onInitialize,
        ) {
            NestedAppContents(
                isInitialized = isInitialized,
                onInitialize = onInitialize,
                index = index + 1,
                content = content,
            )
        }
    } else {
        content()
    }
}
