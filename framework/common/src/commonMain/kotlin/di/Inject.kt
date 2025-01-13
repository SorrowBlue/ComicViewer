package di

import org.koin.core.annotation.Named

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
expect annotation class Inject()

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Named
annotation class IoDispatcher
