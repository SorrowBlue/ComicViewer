/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SecuritySettingsTest {

    @Test
    fun securitySettingsToString_masksNonNullPassword() {
        val settings = SecuritySettings(
            password = "my-secret-pin",
            useBiometrics = true,
            lockOnBackground = false,
        )

        val stringValue = settings.toString()

        assertFalse(stringValue.contains("my-secret-pin"))
        assertEquals(
            "SecuritySettings(password=***, useBiometrics=true, lockOnBackground=false)",
            stringValue,
        )
    }

    @Test
    fun securitySettingsToString_displaysNullWhenPasswordIsNull() {
        val settings = SecuritySettings(
            password = null,
            useBiometrics = false,
            lockOnBackground = true,
        )

        assertEquals(
            "SecuritySettings(password=null, useBiometrics=false, lockOnBackground=true)",
            settings.toString(),
        )
    }
}
