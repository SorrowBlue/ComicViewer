package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
@Preview(
    name = "1 Phone - Portrait",
    group = "Phone",
    device = Devices.PHONE,
    showBackground = true,
)
@Preview(
    name = "2 Phone - Landscape",
    group = "Phone",
    device = "${Devices.PHONE},orientation=landscape",
    showBackground = true,
)
@Preview(
    name = "3 Tablet - Landscape",
    group = "Tablet",
    device = Devices.TABLET,
    showBackground = true,
)
@Preview(
    name = "3 Tablet - Portrait",
    group = "Tablet",
    device = "${Devices.TABLET},orientation=portrait",
    showBackground = true,
)
annotation class PreviewMultiplatform
