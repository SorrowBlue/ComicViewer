package com.sorrowblue.comicviewer.app

import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.koinConfiguration

internal actual fun mainApplication(): KoinAppDeclaration {
    return MainApp.koinConfiguration {  }
}
