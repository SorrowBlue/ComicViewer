/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import com.android.build.api.dsl.ManagedDevices
import com.android.build.api.dsl.ManagedVirtualDevice
import com.android.build.api.dsl.TestExtension

/**
 * Configures standard Gradle Managed Devices (GMD) for Android test modules.
 */
fun TestExtension.configureManagedDevices() {
    testOptions.managedDevices.configureLocalDevices()
}

private fun ManagedDevices.configureLocalDevices() {
    localDevices.apply {
        @Suppress("UnstableApiUsage")
        create("pixel9api37") {
            device = "Pixel 9"
            apiLevel = 37
            systemImageSource = "aosp-atd"
            pageAlignment = ManagedVirtualDevice.PageAlignment.FORCE_16KB_PAGES
        }
        @Suppress("UnstableApiUsage")
        create("pixel9api35") {
            device = "Pixel 9"
            apiLevel = 35
            systemImageSource = "aosp-atd"
        }
        @Suppress("UnstableApiUsage")
        create("pixel7pro35") {
            device = "Pixel 7 Pro"
            apiLevel = 35
            systemImageSource = "aosp-atd"
        }
    }
}
