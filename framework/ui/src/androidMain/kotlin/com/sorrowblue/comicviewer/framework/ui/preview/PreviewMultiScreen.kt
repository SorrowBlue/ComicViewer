package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(
    name = "1 Phone - Portrait",
    group = "Phone",
    device = "spec:parent=pixel_9",
    showBackground = true
)
@Preview(
    name = "2 Phone - Landscape",
    group = "Phone",
    device = "spec:parent=pixel_9,orientation=landscape",
    showBackground = true
)
@Preview(
    name = "3 Tablet - Landscape",
    group = "Tablet",
    device = "id:pixel_tablet",
    showBackground = true
)
@Preview(
    name = "3 Tablet - Portrait",
    group = "Tablet",
    device = "spec:parent=pixel_tablet,orientation=portrait",
    showBackground = true
)
annotation class PreviewMultiScreen
