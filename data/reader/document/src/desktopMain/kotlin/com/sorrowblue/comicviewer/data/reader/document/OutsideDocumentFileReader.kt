package com.sorrowblue.comicviewer.data.reader.document

import com.sorrowblue.comicviewer.plugin.pdf.FileReader
import com.sorrowblue.comicviewer.plugin.pdf.ISeekableInputStream
import java.io.File
import java.lang.reflect.Method
import java.net.URLClassLoader
import logcat.asLog
import logcat.logcat

internal object OutsideDocumentFileReader {
    lateinit var instance: Any
    private var inited: Boolean = false
    private var getReader: Method? = null

    @Synchronized
    fun initLoad(path: String) {
        if (inited) return
        val jarFile = File(path)
        logcat { "$jarFile" }
        val classLoader = URLClassLoader(arrayOf(jarFile.toURI().toURL()))

        runCatching {
            val fqn = "com.sorrowblue.comicviewer.plugin.pdf.PdfPlugin"
            val singletonClass = classLoader.loadClass(fqn)
            instance = singletonClass.getField("INSTANCE").get(null)
            val methodWithoutArgs = singletonClass.getMethod("init")
            methodWithoutArgs.invoke(instance)
            classLoader.loadClass("com.sorrowblue.comicviewer.plugin.pdf.PdfPlugin").apply {
                getReader = singletonClass.getMethod(
                    "getReader",
                    ISeekableInputStream::class.java,
                    String::class.java
                )
            }
            inited = true
        }.onFailure {
            logcat { it.asLog() }
        }
    }

    fun getReader(
        seekableInputStream: ISeekableInputStream,
        magic: String,
    ): FileReader {
        return getReader!!.invoke(
            instance,
            seekableInputStream,
            magic
        ) as FileReader
    }
}
