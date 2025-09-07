package com.sorrowblue.comicviewer.data.database

import org.koin.ksp.generated.startKoin

internal actual fun startTestApp() {
    TestApp.startKoin {
    }
}
