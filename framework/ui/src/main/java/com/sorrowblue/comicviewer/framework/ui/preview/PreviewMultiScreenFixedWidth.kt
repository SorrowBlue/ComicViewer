package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(
    name = "Phone",
    group = "Phone",
    device = "spec:parent=pixel_8",
    showBackground = false,
    apiLevel = 34,
    widthDp = 300
)
@Preview(
    name = "Phone - Landscape",
    group = "Phone",
    device = "spec:parent=pixel_8,orientation=landscape",
    showBackground = false,
    apiLevel = 34,
    widthDp = 300
)
@Preview(
    name = "Unfolded Foldable",
    showBackground = false,
    apiLevel = 34,
    device = "spec:width=673dp,height=841dp",
    widthDp = 300
)
@Preview(
    name = "Tablet",
    device = "id:pixel_tablet",
    showBackground = false,
    apiLevel = 34,
    widthDp = 300
)
@Preview(
    name = "Desktop",
    showBackground = false,
    apiLevel = 34,
    device = "id:desktop_medium",
    widthDp = 300
)
annotation class PreviewMultiScreenFixedWidth
