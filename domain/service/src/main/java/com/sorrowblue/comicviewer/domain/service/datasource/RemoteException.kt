package com.sorrowblue.comicviewer.domain.service.datasource

sealed class RemoteException : RuntimeException() {

    class NoNetwork : RemoteException()
    class InvalidAuth : RemoteException()
    class InvalidServer : RemoteException()
    class NotFound : RemoteException()
    class Unknown : RemoteException()
}
