package com.sorrowblue.comicviewer.plugin.aidl;

@Backing(type="int")
enum CompressFormat {
    JPEG = 0,
    PNG = 1,
    WEBP_LOSSY = 3,
    WEBP_LOSSLESS = 4
}
