package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "phone", device = "id:pixel_7")
@Preview(name = "landscape", device = "spec:parent=pixel_7,orientation=landscape")
@Preview(name = "tablet", device = "id:pixel_tablet")
annotation class PreviewComic

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Preview(name = "landscape", device = "spec:shape=Normal,width=640,height=360,unit=dp,dpi=480")
annotation class PreviewMobile
