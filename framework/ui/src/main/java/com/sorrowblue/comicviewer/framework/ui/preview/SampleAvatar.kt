package com.sorrowblue.comicviewer.framework.ui.preview

import com.sorrowblue.comicviewer.framework.ui.R

internal val nextSampleAvatar
    get() = avatarList[AvatarIndex++].apply {
        if (AvatarIndex >= avatarList.size) {
            AvatarIndex = 0
        }
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
