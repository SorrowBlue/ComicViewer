package com.sorrowblue.comicviewer.data.exception

sealed class RemoteException : RuntimeException() {

    object NoNetwork : RemoteException()
    object InvalidAuth : RemoteException()
    object InvalidServer : RemoteException()
    object NotFound : RemoteException()
}
