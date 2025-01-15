package com.sorrowblue.comicviewer.data.di

import com.sorrowblue.comicviewer.data.database.DataDatabaseModule
import com.sorrowblue.comicviewer.domain.service.DomainServiceModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DomainServiceModule::class, DataDatabaseModule::class])
@ComponentScan
class DiModule
