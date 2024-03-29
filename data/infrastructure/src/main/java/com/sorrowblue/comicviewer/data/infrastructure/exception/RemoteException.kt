package com.sorrowblue.comicviewer.data.infrastructure.exception

sealed class RemoteException : RuntimeException() {

    data object NoNetwork : RemoteException()
    data object InvalidAuth : RemoteException()
    data object InvalidServer : RemoteException()
    data object NotFound : RemoteException()
    data object Unknown : RemoteException()
}
