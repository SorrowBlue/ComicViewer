package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.BaseRequest
import com.sorrowblue.comicviewer.domain.entity.Book
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.ServerId

enum class GetNextComicRel {
    NEXT, PREV
}

class GetNextComicRequest(val serverId: ServerId, val path: String, val relation: GetNextComicRel) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class GetNextComicUseCase : OneTimeUseCase2<GetNextComicRequest, Book, GetLibraryInfoError>()

class GetLibraryInfoRequest(val serverId: ServerId) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class ServerBookshelfUseCase : FlowUseCase<GetLibraryInfoRequest, ServerBookshelf, GetLibraryInfoError>()

enum class GetLibraryInfoError {
    NOT_FOUND,
    SYSTEM_ERROR
}

class GetFileRequest(val serverId: ServerId, val path: String) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class GetFileUseCase : FlowOneUseCase<GetFileRequest, File, GetLibraryInfoError>()
