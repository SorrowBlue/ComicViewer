/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.bookshelf

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SmbServerTest {

    @Test
    fun usernamePasswordToString_masksPassword() {
        val auth = SmbServer.Auth.UsernamePassword(
            domain = "WORKGROUP",
            username = "alice",
            password = "super-secret-password",
        )

        val stringValue = auth.toString()

        assertFalse(stringValue.contains("super-secret-password"))
        assertEquals(
            "UsernamePassword(domain=WORKGROUP, username=alice, password=***)",
            stringValue,
        )
    }
}
