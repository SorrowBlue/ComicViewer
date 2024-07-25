package com.sorrowblue.comicviewer.framework.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource

@Composable
fun previewPainter(): Painter {
    return painterResource(id = avatarList[AvatarIndex++]).also {
        if (AvatarIndex >= avatarList.size) {
            AvatarIndex = 0
        }
    }
}

@Composable
fun previewPlaceholder(): Painter? =
    if (LocalInspectionMode.current) {
        previewPainter()
    } else {
        null
    }

private var AvatarIndex = 0

private val avatarList = listOf(
    R.drawable.avatar_1,
    R.drawable.avatar_2,
    R.drawable.avatar_3,
    R.drawable.avatar_4,
    R.drawable.avatar_5,
    R.drawable.avatar_6,
    R.drawable.avatar_7,
    R.drawable.avatar_8,
    R.drawable.avatar_9,
    R.drawable.avatar_10,
    R.drawable.avatar_11,
    R.drawable.avatar_12,
    R.drawable.avatar_13,
    R.drawable.avatar_14,
    R.drawable.avatar_15,
    R.drawable.avatar_16,
)
