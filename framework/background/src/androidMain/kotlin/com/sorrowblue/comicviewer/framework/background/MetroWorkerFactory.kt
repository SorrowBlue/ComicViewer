package com.sorrowblue.comicviewer.framework.background

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DefaultBinding
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metro.Inject
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
@Inject
internal class MetroWorkerFactory(
    val workerProviders: Map<KClass<out ListenableWorker>, MetroWorkerInstanceFactory<*>>,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? =
        workerProviders[Class.forName(workerClassName).kotlin]?.create(workerParameters)
}

@OptIn(ExperimentalMetroApi::class)
@DefaultBinding<MetroWorkerInstanceFactory<*>>()
interface MetroWorkerInstanceFactory<T : ListenableWorker> {
    fun create(params: WorkerParameters): T
}
