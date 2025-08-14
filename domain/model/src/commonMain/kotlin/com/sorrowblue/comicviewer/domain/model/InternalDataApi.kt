package com.sorrowblue.comicviewer.domain.model

/**
 * dataモジュール内部での利用を意図したAPIであることを示すマーカーアノテーション。
 * このアノテーションが付いたAPIを他のモジュールで利用するには、
 *
 * @optIn(InternalDataApi::class) の宣言が必要です。
 */
@RequiresOptIn(
    message = "This is an internal API for the data module. Do not use it in other modules.",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalDataApi
