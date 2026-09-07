/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.startup

import dev.zacsweers.metro.DefaultBinding
import kotlin.reflect.KClass

@DefaultBinding<Initializer<*>>
interface Initializer<T> {
    fun create(): T

    fun dependencies(): List<KClass<out Initializer<*>>?>

    companion object {
        fun initialize(list: List<Initializer<*>>) {
            var initializing = list
            val initialized = mutableSetOf<KClass<out Initializer<*>>>()
            while (initializing.isNotEmpty()) {
                val size = initializing.size
                initializing = initializing.filterNot { initializer ->
                    if (initialized.containsAll(initializer.dependencies())) {
                        initializer.create()
                        initialized.add(initializer::class)
                        true
                    } else {
                        false
                    }
                }
                if (size == initializing.size) {
                    throw StartupException("!!!Circular Dependencies!!!")
                }
            }
        }
    }
}

internal class StartupException(message: String?) : RuntimeException(message)
