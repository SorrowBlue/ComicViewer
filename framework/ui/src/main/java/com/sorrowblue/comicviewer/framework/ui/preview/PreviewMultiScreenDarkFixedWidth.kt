package com.sorrowblue.comicviewer.framework.ui.preview

import android.content.res.Configuration
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
    widthDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Phone - Landscape",
    group = "Phone",
    device = "spec:parent=pixel_8,orientation=landscape",
    showBackground = false,
    apiLevel = 34,
    widthDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Unfolded Foldable",
    showBackground = false,
    apiLevel = 34,
    device = "spec:width=673dp,height=841dp",
    widthDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Tablet",
    device = "id:pixel_tablet",
    showBackground = false,
    apiLevel = 34,
    widthDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    name = "Desktop",
    showBackground = false,
    apiLevel = 34,
    device = "id:desktop_medium",
    widthDp = 300,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
annotation class PreviewMultiScreenDarkFixedWidth
