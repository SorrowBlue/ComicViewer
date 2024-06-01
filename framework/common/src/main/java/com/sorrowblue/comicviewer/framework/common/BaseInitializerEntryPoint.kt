package com.sorrowblue.comicviewer.framework.common

import android.content.Context
import dagger.hilt.android.EntryPointAccessors

interface BaseInitializerEntryPoint<T> {

    interface CompanionObject {

        fun <T> resolve(context: Context): BaseInitializerEntryPoint<T> {
            val appContext = checkNotNull(context.applicationContext)
            @Suppress("UNCHECKED_CAST")
            return EntryPointAccessors.fromApplication(
                appContext,
                BaseInitializerEntryPoint::class.java
            ) as BaseInitializerEntryPoint<T>
        }
    }

    fun inject(initializer: T)
}
