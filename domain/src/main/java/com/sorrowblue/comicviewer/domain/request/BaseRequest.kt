package com.sorrowblue.comicviewer.domain.request

interface BaseRequest {
    fun validate(): Boolean
}
