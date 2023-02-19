package com.sorrowblue.comicviewer.library.box.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    private val Context.boxConnectionStateDataStore: DataStore<BoxConnectionState> by dataStore(
        fileName = "box_connection_state.pb",
        serializer = BoxConnectionState.Serializer()
    )

    @Singleton
    @Provides
    fun provideBoxConnectionStateDataStore(@ApplicationContext context: Context): DataStore<BoxConnectionState> =
        context.boxConnectionStateDataStore
}
