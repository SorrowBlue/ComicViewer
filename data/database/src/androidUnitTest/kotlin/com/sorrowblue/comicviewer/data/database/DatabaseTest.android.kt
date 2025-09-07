package com.sorrowblue.comicviewer.data.database

import androidx.test.platform.app.InstrumentationRegistry
import org.koin.android.ext.koin.androidContext
import org.koin.ksp.generated.startKoin

internal actual fun startTestApp() {
    TestApp.startKoin {
        androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
    }
}
