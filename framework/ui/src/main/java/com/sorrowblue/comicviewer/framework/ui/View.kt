package com.sorrowblue.comicviewer.framework.ui

import android.view.View
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

fun View.autoDispose(job: Job) {
    val listener = ViewListener(this, job)
    this.addOnAttachStateChangeListener(listener)
}

private class ViewListener(
    private val view: View,
    private val job: Job
) : View.OnAttachStateChangeListener,
    CompletionHandler {
    override fun onViewDetachedFromWindow(v: View) {
        view.removeOnAttachStateChangeListener(this)
        job.cancel()
    }

    override fun onViewAttachedToWindow(v: View) {
        // do nothing
    }

    override fun invoke(cause: Throwable?) {
        view.removeOnAttachStateChangeListener(this)
        job.cancel()
    }
}

@Suppress("FunctionName")
fun ViewAutoDisposeInterceptor(view: View): ContinuationInterceptor =
    ViewAutoDisposeInterceptorImpl(view)

/**
 * Create a ContinuationInterceptor that follows attach/detach lifecycle of [View].
 */
fun View.autoDisposeInterceptor(): ContinuationInterceptor =
    ViewAutoDisposeInterceptor(this)

private class ViewAutoDisposeInterceptorImpl(
    private val view: View
) : ContinuationInterceptor {
    override val key: CoroutineContext.Key<*>
        get() = ContinuationInterceptor

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        val job = continuation.context[Job]
        if (job != null) {
            view.autoDispose(job)
        }
        return continuation
    }
}

private val TAG = R.id.framework_ui_autodispose_view_tag

val View.autoDisposeScope: CoroutineScope
    get() {
        val exist = getTag(TAG) as? CoroutineScope
        if (exist != null) {
            return exist
        }
        val newScope =
            ViewCoroutineScope(SupervisorJob() + Dispatchers.Main + autoDisposeInterceptor())
        setTag(TAG, newScope)
        return newScope
    }

internal class ViewCoroutineScope(
    override val coroutineContext: CoroutineContext
) : CoroutineScope
