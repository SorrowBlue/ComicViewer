/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.common

actual typealias PlatformContext = IosContext

@Suppress("UtilityClassWithPublicConstructor")
abstract class IosContext {

    companion object {
        private val INSTANCE = IosContextImpl()
        operator fun invoke(): IosContext = INSTANCE
    }
}

private class IosContextImpl : IosContext()
