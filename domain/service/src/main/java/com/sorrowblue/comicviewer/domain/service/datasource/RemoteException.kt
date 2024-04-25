package com.sorrowblue.comicviewer.domain.service.datasource

sealed class RemoteException : RuntimeException() {

    data object NoNetwork : RemoteException()
    data object InvalidAuth : RemoteException()
    data object InvalidServer : RemoteException()
    data object NotFound : RemoteException()
    data object Unknown : RemoteException()
}
