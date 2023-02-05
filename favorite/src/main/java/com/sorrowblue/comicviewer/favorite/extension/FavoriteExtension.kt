package com.sorrowblue.comicviewer.favorite.extension

import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite

val Favorite.transitionName get() = "transition_name=${id.value}"
