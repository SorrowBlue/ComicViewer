package com.sorrowblue.comicviewer.favorite.extensiton

import com.sorrowblue.comicviewer.domain.entity.Favorite

val Favorite.transitionName get() = "transition_name=${id.value}"
