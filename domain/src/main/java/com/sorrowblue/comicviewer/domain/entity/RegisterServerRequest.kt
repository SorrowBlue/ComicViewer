package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.model.BaseRequest

class RegisterServerRequest(val server: Server, val path: String) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}
