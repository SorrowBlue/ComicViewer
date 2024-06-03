package com.sorrowblue.comicviewer.app

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger
import logcat.logcat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class MainActivityTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        LogcatLogger.install(AndroidLogcatLogger())
    }

    @Test
    fun getXxxAttrTest() {
        // Activity を起動
        launchActivity<MainActivity>().use { scenario ->
            scenario.onActivity { activity ->
                logcat { "activity.javaClass.simpleName = ${activity.javaClass.simpleName}" }
            }
        }
    }
}
