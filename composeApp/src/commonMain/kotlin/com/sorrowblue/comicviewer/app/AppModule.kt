package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.aggregation.di.AggregationModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@Module(includes = [AggregationModule::class])
@Configuration
@ComponentScan("com.sorrowblue.comicviewer.app")
internal class AppModule

@KoinApplication
internal object MainApp
