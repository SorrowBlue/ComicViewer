package com.sorrowblue.comicviewer.app.benchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures the startup time of the ComicViewer app.
 *
 * Run via: `./gradlew :android:app:benchmark:connectedBenchmarkAndroidTest`
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /**
     * Cold startup: app process is killed before each iteration.
     * Simulates the first launch after install or force-stop.
     */
    @Test
    fun startupCold() = benchmarkRule.measureRepeated(
        packageName = BuildConfig.targetPackage,
        metrics = listOf(StartupTimingMetric()),
        startupMode = StartupMode.COLD,
        iterations = 5,
    ) {
        startActivityAndWait()
    }

    /**
     * Warm startup: app process stays alive but Activity is recreated.
     * Simulates returning to the app after it was sent to background.
     */
    @Test
    fun startupWarm() = benchmarkRule.measureRepeated(
        packageName = BuildConfig.targetPackage,
        metrics = listOf(StartupTimingMetric()),
        startupMode = StartupMode.WARM,
        iterations = 5,
    ) {
        startActivityAndWait()
    }
}
