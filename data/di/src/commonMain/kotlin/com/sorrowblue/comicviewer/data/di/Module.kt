package com.sorrowblue.comicviewer.data.di

import com.sorrowblue.comicviewer.data.coil.di.DataCoilModule
import com.sorrowblue.comicviewer.data.database.di.DataDatabaseModule
import com.sorrowblue.comicviewer.data.datastore.di.DataDataStoreModule
import com.sorrowblue.comicviewer.data.reader.zip.di.DataReaderZipModule
import com.sorrowblue.comicviewer.data.storage.client.di.DataStorageClientModule
import com.sorrowblue.comicviewer.data.storage.device.di.DataStorageDeviceModule
import com.sorrowblue.comicviewer.data.storage.smb.di.DataStorageSmbModule
import com.sorrowblue.comicviewer.domain.service.DomainServiceModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        DomainServiceModule::class,
        DataCoilModule::class,
        DataDatabaseModule::class,
        DataDataStoreModule::class,
        DataReaderZipModule::class,
        DataStorageClientModule::class,
        DataStorageSmbModule::class,
        DataStorageDeviceModule::class,
    ]
)
@ComponentScan("com.sorrowblue.comicviewer.data.di")
class DiModule
