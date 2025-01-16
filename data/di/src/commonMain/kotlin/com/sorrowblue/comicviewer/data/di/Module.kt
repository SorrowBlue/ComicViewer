package com.sorrowblue.comicviewer.data.di

import com.sorrowblue.comicviewer.data.coil.di.DataCoilModule
import com.sorrowblue.comicviewer.data.database.di.DataDatabaseModule
import com.sorrowblue.comicviewer.data.datastore.di.DataDatastoreModule
import com.sorrowblue.comicviewer.data.storage.client.di.DataStorageClientModule
import com.sorrowblue.comicviewer.domain.service.DomainServiceModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DomainServiceModule::class, DataDatabaseModule::class, DataDatastoreModule::class, DataCoilModule::class, DataStorageClientModule::class])
@ComponentScan
class DiModule
