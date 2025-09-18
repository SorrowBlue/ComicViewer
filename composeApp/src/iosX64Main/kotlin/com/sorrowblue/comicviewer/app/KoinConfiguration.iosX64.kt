package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.aggregation.di.AggregationModule
import org.koin.ksp.generated.module

internal actual fun allModules() = listOf(
    AggregationModule().module,
    AppModule().module
)
