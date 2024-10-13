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
    showBackground = true,
    apiLevel = 34
)
@Preview(
    name = "Phone - Landscape",
    group = "Phone",
    device = "spec:parent=pixel_8,orientation=landscape",
    showBackground = true,
    apiLevel = 34
)
@Preview(
    name = "Unfolded Foldable",
    showBackground = true,
    apiLevel = 34,
    device = "spec:width=673dp,height=841dp"
)
@Preview(name = "Tablet", device = "id:pixel_tablet", showBackground = true, apiLevel = 34)
@Preview(
    name = "Desktop",
    showBackground = true,
    apiLevel = 34,
    device = "id:desktop_medium"
)
annotation class PreviewMultiScreen
