/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import com.sorrowblue.comicviewer.framework.test.SmallTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SmallTest
class PinDrawableTest {

    @Test
    fun `all PinShapes have matching node counts and interpolate cleanly`() {
        assertEquals(4, PinShapes.size)
        PinShapes.forEach { shape ->
            assertEquals(shape.fromNodes.size, shape.toNodes.size)
            assertEquals(shape.fromNodes, lerpPathNodes(shape.fromNodes, shape.toNodes, 0f))
            assertEquals(shape.toNodes, lerpPathNodes(shape.fromNodes, shape.toNodes, 1f))
            val midpoint = lerpPathNodes(shape.fromNodes, shape.toNodes, 0.5f)
            assertEquals(shape.fromNodes.size, midpoint.size)
        }
    }
}
