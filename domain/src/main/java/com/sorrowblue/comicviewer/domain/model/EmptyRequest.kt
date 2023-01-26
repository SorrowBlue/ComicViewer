package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.request.BaseRequest

object EmptyRequest : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}
