package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(name = "Phone", device = Devices.PHONE, showBackground = true)
@Preview(
    name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    showBackground = true
)
@Preview(name = "Unfolded Foldable", device = Devices.FOLDABLE, showBackground = true)
@Preview(name = "Tablet", device = Devices.TABLET, showBackground = true)
@Preview(name = "Desktop", device = Devices.DESKTOP, showBackground = true)
annotation class PreviewMultiScreen
