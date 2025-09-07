package com.sorrowblue.comicviewer.data.di

import com.sorrowblue.comicviewer.domain.model.PluginManager
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.sorrowblue.comicviewer")
class DiModule

@Single
fun providePluginManager(): PluginManager = PluginManager()
