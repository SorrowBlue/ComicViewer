package com.sorrowblue.comicviewer.app

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger

@DependencyGraph(scope = AppScope::class)
internal interface TestAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createTestAppGraph(@Provides applicationContext: PlatformContext): TestAppGraph
    }
}

class TestApplication :
    Application(),
    AppGraphProvider<AppGraph>,
    Configuration.Provider {

    override val appGraph: AppGraph by lazy {
        createGraphFactory<TestAppGraph.Factory>().createTestAppGraph(
            InstrumentationRegistry.getInstrumentation().context,
        )
    }

    init {
        LogcatLogger.install(AndroidLogcatLogger(LogPriority.VERBOSE))
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()
}
