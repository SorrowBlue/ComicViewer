package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.ServerBook
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.ServerFile
import com.sorrowblue.comicviewer.domain.entity.ServerId
import com.sorrowblue.comicviewer.domain.model.BaseRequest

class GetServerBookRequest(val serverId: ServerId, val path: String) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class GetServerBookUseCase :
    FlowOneUseCase<GetServerBookRequest, ServerBook, GetLibraryFileResult>()

class GetServerFileRequest(val serverId: ServerId, val path: String) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class GetServerFileUseCase :
    FlowOneUseCase<GetServerFileRequest, ServerFile, GetLibraryFileResult>()

class GetServerBookshelfRequest(val serverId: ServerId, val path: String) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class GetServerBookshelfUseCase :
    FlowOneUseCase<GetServerBookshelfRequest, ServerBookshelf, GetLibraryFileResult>()
