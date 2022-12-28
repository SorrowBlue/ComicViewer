package com.sorrowblue.comicviewer.framework.settings

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class AndroidLifecycleBindingProperty<T, V> : ReadOnlyProperty<T, V> {

    private var binding: V? = null

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        binding?.let { return it }
        val lifecycle = getLifecycleOwner(thisRef).lifecycle
        val binding = bind(thisRef)
        if (lifecycle.currentState != Lifecycle.State.DESTROYED) {
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) = clear()
            })
            if (binding is ViewDataBinding) {
                binding.lifecycleOwner = getLifecycleOwner(thisRef)
            }
            this.binding = binding
        }
        return binding
    }

    protected abstract fun bind(thisRef: T): V

    protected abstract fun getLifecycleOwner(thisRef: T): LifecycleOwner

    @MainThread
    private fun clear() {
        mainHandler.post {
            binding = null
        }
    }

    companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }
}
