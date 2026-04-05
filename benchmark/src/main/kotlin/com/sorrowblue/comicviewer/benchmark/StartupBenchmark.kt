package com.sorrowblue.comicviewer.benchmark

import androidx.benchmark.macro.CompilationMode
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
 * Run via: `./gradlew :benchmark:connectedBenchmarkAndroidTest`
 *
 * Results include:
 * - `timeToInitialDisplayMs`: time until the first frame is displayed (TTID)
 * - `timeToFullDisplayMs`: time until the app reports fully drawn (TTFD)
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
        packageName = BuildConfig.TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.Full(),
        startupMode = StartupMode.COLD,
        iterations = 5,
    ) {
        pressHome()
        startActivityAndWait()
    }

    /**
     * Warm startup: app process stays alive but Activity is recreated.
     * Simulates returning to the app after it was sent to background.
     */
    @Test
    fun startupWarm() = benchmarkRule.measureRepeated(
        packageName = BuildConfig.TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.Full(),
        startupMode = StartupMode.WARM,
        iterations = 5,
    ) {
        startActivityAndWait()
    }

    /**
     * Hot startup: app process and Activity are both alive.
     * Simulates bringing the app to foreground from the Recents screen.
     */
    @Test
    fun startupHot() = benchmarkRule.measureRepeated(
        packageName = BuildConfig.TARGET_PACKAGE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.Full(),
        startupMode = StartupMode.HOT,
        iterations = 5,
    ) {
        startActivityAndWait()
    }
}
