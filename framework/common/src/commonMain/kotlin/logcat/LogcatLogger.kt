package logcat

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.atomicfu.update

interface LogcatLogger {

    /**
     * Whether a log with the provided priority should be logged and the
     * corresponding message providing lambda evaluated. Called by [logcat].
     */
    fun isLoggable(priority: LogPriority) = true

    /** Write a log to its destination. Called by [logcat]. */
    fun log(
        priority: LogPriority,
        tag: String,
        message: String,
    )

    companion object : SynchronizedObject() {

        private var aLogger: AtomicRef<LogcatLogger> = atomic(NoLog)

        @PublishedApi
        internal var logger: LogcatLogger
            get() = aLogger.value
            private set(value) = aLogger.update { value }

        private var aInstalledThrowable: AtomicRef<Throwable?> = atomic(null)
        private var installedThrowable: Throwable?
            get() = aInstalledThrowable.value
            set(value) = aInstalledThrowable.update { value }

        val isInstalled: Boolean
            get() = installedThrowable != null

        /**
         * Installs a [LogcatLogger].
         *
         * It is an error to call [install] more than once without calling
         * [uninstall] in between, however doing this won't throw, it'll log an
         * error to the newly provided logger.
         */
        fun install(logger: LogcatLogger) {
            synchronized(this) {
                if (isInstalled) {
                    logger.log(
                        LogPriority.ERROR,
                        "LogcatLogger",
                        "Installing $logger even though a logger was previously installed here: " +
                            installedThrowable!!.asLog()
                    )
                }
                installedThrowable = RuntimeException("Previous logger installed here")
                Companion.logger = logger
            }
        }

        /** Replaces the current logger (if any) with a no-op logger. */
        fun uninstall() {
            synchronized(this) {
                installedThrowable = null
                logger = NoLog
            }
        }
    }

    private object NoLog : LogcatLogger {
        override fun isLoggable(priority: LogPriority) = false

        override fun log(
            priority: LogPriority,
            tag: String,
            message: String,
        ) = error("Should never receive any log")
    }
}
