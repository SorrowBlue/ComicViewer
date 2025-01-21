package com.sorrowblue.comicviewer.data.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
//    includes = [
//        DomainServiceModule::class,
//        DataCoilModule::class,
//        DataDatabaseModule::class,
//        DataDataStoreModule::class,
//        DataReaderZipModule::class,
//        DataStorageClientModule::class,
//        DataStorageSmbModule::class,
//        DataStorageDeviceModule::class,
//    ]
)
@ComponentScan("com.sorrowblue.comicviewer")
class DiModule
