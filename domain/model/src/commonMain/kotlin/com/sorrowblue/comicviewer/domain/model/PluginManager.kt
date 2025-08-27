package com.sorrowblue.comicviewer.domain.model

class PluginManager {

    interface Callback {
        fun onError(msg: String)
    }

    private val callbacks = mutableListOf<Callback>()

    fun addCallback(callback: Callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
    }

    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    fun onError(msg: String) {
        callbacks.forEach { it.onError(msg) }
    }
}
