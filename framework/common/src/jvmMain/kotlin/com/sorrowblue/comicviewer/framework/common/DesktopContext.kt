package com.sorrowblue.comicviewer.framework.common

@Suppress("AbstractClassCanBeInterface", "UtilityClassWithPublicConstructor")
abstract class DesktopContext {

    companion object {
        private val INSTANCE = DesktopContextImpl()
        operator fun invoke(): DesktopContext = INSTANCE
    }
}

private class DesktopContextImpl : DesktopContext()
