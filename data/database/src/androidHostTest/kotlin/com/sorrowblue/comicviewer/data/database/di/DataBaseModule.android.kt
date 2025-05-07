package com.sorrowblue.comicviewer.data.database.di

import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.module

internal actual fun configurationKoinForTest() = KoinConfiguration {
    modules(DatabaseTestModule().module)
}
