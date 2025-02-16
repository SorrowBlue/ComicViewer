package com.sorrowblue.comicviewer.framework.common

import kotlin.reflect.KClass

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
                    throw RuntimeException("!!!Circular Dependencies!!!")
                }
            }
        }
    }
}
