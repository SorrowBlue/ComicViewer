package com.sorrowblue.comicviewer.data.datastore.startup

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface InitializerEntryPoint {

    companion object {
        fun resolve(context: Context): InitializerEntryPoint {
            val appContext = checkNotNull(context.applicationContext)
            return EntryPointAccessors.fromApplication(
                appContext,
                InitializerEntryPoint::class.java
            )
        }
    }

    fun inject(initializer: DarkModeInitializer)
    fun inject(initializer: ExtensionInitializer)
}
