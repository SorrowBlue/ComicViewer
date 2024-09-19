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
    device = "spec:id=reference_foldable,shape=Normal,width=673,height=841,unit=dp,dpi=420",
    showBackground = true,
    apiLevel = 34
)
@Preview(name = "Tablet", device = "id:pixel_tablet", showBackground = true, apiLevel = 34)
@Preview(
    name = "Desktop",
    device = "spec:id=reference_desktop,shape=Normal,width=1920,height=1080,unit=dp,dpi=160",
    showBackground = true,
    apiLevel = 34
)
annotation class PreviewMultiScreen
