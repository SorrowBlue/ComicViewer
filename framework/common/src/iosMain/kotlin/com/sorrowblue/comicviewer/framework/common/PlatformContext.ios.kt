package com.sorrowblue.comicviewer.framework.common

actual typealias PlatformContext = IosContext

@Suppress("UtilityClassWithPublicConstructor")
abstract class IosContext

class IosContextImpl : IosContext()
