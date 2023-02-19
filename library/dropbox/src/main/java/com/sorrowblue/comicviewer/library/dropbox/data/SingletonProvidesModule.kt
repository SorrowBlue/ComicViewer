package com.sorrowblue.comicviewer.library.dropbox.data

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

    private val Context.dropboxCredentialDataStore: DataStore<DropboxCredential> by dataStore(
        fileName = "dropbox_credential.pb",
        serializer = DropboxCredential.Serializer()
    )

    @Singleton
    @Provides
    fun provideDropboxCredentialDataStore(@ApplicationContext context: Context): DataStore<DropboxCredential> =
        context.dropboxCredentialDataStore
}
