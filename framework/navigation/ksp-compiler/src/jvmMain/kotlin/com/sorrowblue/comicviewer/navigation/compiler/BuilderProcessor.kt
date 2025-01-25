package com.sorrowblue.comicviewer.navigation.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toClassNameOrNull
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass
import kotlin.reflect.KType

private const val Destination = "com.sorrowblue.comicviewer.framework.annotation.Destination"
private val Serializable = ClassName("kotlinx.serialization", "Serializable")
private val ScreenDestination =
    ClassName("com.sorrowblue.comicviewer.framework.navigation", "ScreenDestination")
private val NavGraph = ClassName("com.sorrowblue.comicviewer.framework.navigation", "NavGraph")
private val NavGraphAnnotation =
    ClassName("com.sorrowblue.comicviewer.framework.annotation", "NavGraph")

private val DestinationInGraph =
    ClassName("com.sorrowblue.comicviewer.framework.annotation", "DestinationInGraph")
private val NestedNavGraph =
    ClassName("com.sorrowblue.comicviewer.framework.annotation", "NestedNavGraph")
private val DestinationStyle =
    ClassName("com.sorrowblue.comicviewer.framework.navigation", "DestinationStyle")
private val Composable = ClassName("androidx.compose.runtime", "Composable")
private val NavBackStackEntry = ClassName("androidx.navigation", "NavBackStackEntry")
private val KoinScope = ClassName("org.koin.compose.scope", "KoinScope")

val KSType.isObjectClass get() = (declaration as? KSClassDeclaration)?.classKind == ClassKind.OBJECT

fun DestinationResolver(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
    resolver: Resolver,
): Sequence<KSAnnotated> {
    val symbols = resolver.getSymbolsWithAnnotation(Destination)
    symbols.forEach { symbol ->
        logger.warn("@Destination checking..")
        if (symbol is KSFunctionDeclaration) {
            val functionName = symbol.simpleName.asString()
            logger.warn("  functionName=$functionName")
            val routeType: KSType
            val styleType: KSType
            symbol.annotations.filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == Destination }
                .first().let {
                    it.annotationType.resolve().arguments.first().type?.resolve()!!.let {
                        logger.warn("    route")
                        routeType = it
                        val routeTypeName = routeType.declaration.qualifiedName?.asString()
                        logger.warn("      class=$routeTypeName")
                        if (routeType.declaration.annotations.any {
                                it.annotationType.resolve()
                                    .toClassName() == Serializable
                            }) {
                            logger.warn(
                                """
                                ${routeType.declaration.qualifiedName?.asString()}
                                ${routeType.declaration is KSClassDeclaration}
                            """.trimIndent()
                            )
                            if (!routeType.isObjectClass) {
                                generateTypeMap(
                                    codeGenerator,
                                    logger,
                                    routeType.declaration as KSClassDeclaration
                                )
                            }
                        } else {
                            logger.error("route is not @Serializable")
                        }
                        routeType
                    }
                    it.arguments.first { it.name?.asString() == "style" }.let {
                        styleType = it.value as KSType
                    }
                }
            val argumentsNoDefault =
                symbol.parameters.filterNot(KSValueParameter::hasDefault).map {
                    it.name!!.asString() to it.type
                }
            val genFunctionName = routeType.toClassName().simpleName + "Destination"
            val clazz = TypeSpec.classBuilder(genFunctionName)
                .addSuperinterface(ScreenDestination)
                .addProperty(
                    PropertySpec.builder(
                        "route",
                        KClass::class.asTypeName().parameterizedBy(STAR),
                        KModifier.OVERRIDE
                    )
                        .initializer("%L::class", routeType.declaration.qualifiedName!!.asString())
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("style", DestinationStyle, KModifier.OVERRIDE)
                        .initializer("%L", styleType.toClassName().canonicalName)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder(
                        "typeMap",
                        Map::class.asClassName().parameterizedBy(
                            KType::class.asTypeName(),
                            ClassName("androidx.navigation", "NavType").parameterizedBy(STAR)
                        ),
                        KModifier.OVERRIDE
                    ).apply {
                        if (routeType.isObjectClass) {
                            initializer("emptyMap()")
                        } else {
                            initializer(
                                "%L.typeMap()",
                                routeType.declaration.qualifiedName!!.asString()
                            )
                        }
                    }.build()
                )
                .addFunction(
                    FunSpec.builder("Content")
                        .addModifiers(KModifier.OVERRIDE)
                        .addAnnotation(Composable)
                        .receiver(NavBackStackEntry)
                        .addStatement(
                            "$functionName(\n%L\n)",
                            argumentsNoDefault.joinToString(",\n") {
                                if (it.second.resolve().declaration.qualifiedName?.asString() == "com.sorrowblue.comicviewer.framework.navigation.NavResultSender") {
                                    val senderType =
                                        it.second.resolve().arguments.first().type!!.resolve().declaration.qualifiedName!!.asString()
                                    "${it.first}=navController.navResultSender<$senderType>(${routeType.declaration.qualifiedName!!.asString()}::class)"
                                } else if (it.second.resolve().declaration.qualifiedName?.asString() == "com.sorrowblue.comicviewer.framework.navigation.NavResultReceiver") {
                                    "${it.first}=navResultReceiver()"
                                } else if (routeType.declaration.qualifiedName!!.asString() == it.second.resolve().declaration.qualifiedName!!.asString()) {
                                    "${it.first}=toRoute()"
                                } else {
                                    "${it.first}=koinInject()"
                                }
                            })
                        .build()
                )
                .build()
            val packageName = routeType.toClassName().packageName

            // route com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf.kt
            // destination com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenDestination.nav.kt
            FileSpec.builder(packageName, "${genFunctionName}.nav")
                .indent("    ")
                .addKotlinDefaultImports()
                .addImport(ScreenDestination, "")
                .addImport(KoinScope, "")
                .addImport("androidx.navigation", "toRoute")
                .addImport("org.koin.compose", "koinInject")
                .addImport("com.sorrowblue.comicviewer.framework.navigation", "navResultSender")
                .addImport("com.sorrowblue.comicviewer.framework.navigation", "navResultReceiver")
                .addType(clazz)
                .build()
                .writeTo(codeGenerator, Dependencies(true))
        }
    }
    return symbols.filter { !it.validate() }
}

fun NavGraphResolver(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
    resolver: Resolver,
): List<KSAnnotated> {
    logger.warn("NavGraph start...")
    val symbols = resolver.getSymbolsWithAnnotation(NavGraphAnnotation.canonicalName)
    val skipSymbols = mutableListOf<KSAnnotated>()
    symbols.forEach { symbol ->
        logger.warn("  symbol is ${symbol}")
        if (symbol is KSClassDeclaration) {
            val route: KSType
            val startDestination: KSType
            val root: KSType
            logger.warn("  isCompanionObject ${symbol.isCompanionObject}")

            // @NavGraph(startDestination=XXX::class)
            route = symbol.asType(emptyList())
            symbol.annotations.first { it.annotationType.resolve().declaration.qualifiedName?.asString() == NavGraphAnnotation.canonicalName }
                .let {
                    startDestination =
                        it.arguments.first { it.name?.asString() == "startDestination" }.value as KSType
                    logger.warn("${(it.arguments.first { it.name?.asString() == "root" }.value)}")
                    root = it.arguments.first { it.name?.asString() == "root" }.value as KSType
                }


            // data class Route(val value: String)
            if (!route.isObjectClass) {
                generateTypeMap(codeGenerator, logger, route.declaration as KSClassDeclaration)
            }
            logger.warn("  route=$route")
            logger.warn("  startDestination=$startDestination")
            val inGraphRoute = mutableListOf<KSType>()
            val nestedNavGraphRoute = mutableListOf<KSType>()

            // @DestinationInGraph
            // @NestedNavGraph
            // companion object
            val includeObject = symbol.declarations.filterIsInstance<KSClassDeclaration>()
                .first { it.classKind == ClassKind.OBJECT }
            includeObject.annotations
                .forEach {
                    logger.warn("  companion object annotation=${it.shortName.asString()}")
                    val resolved = it.annotationType.resolve()
                    if (resolved.declaration.qualifiedName?.asString() == DestinationInGraph.canonicalName) {
                        inGraphRoute.add(resolved.arguments.first().type!!.resolve())
                    } else if (resolved.declaration.qualifiedName?.asString() == NestedNavGraph.canonicalName) {
                        nestedNavGraphRoute.add(resolved.arguments.first().type!!.resolve())
                    }
                }

            val isRoot = root.toClassName() != ClassName("java.lang", "Void")
            val className =
                if (isRoot) {
                    root.declaration.simpleName.asString()
                } else {
                    route.declaration.simpleName.asString() + "NavGraph"
                }
            val clazz = TypeSpec.classBuilder(className)
                .apply {
                    if (isRoot) {
                        this.addModifiers(KModifier.ACTUAL)
                    }
                }
                .addSuperinterface(NavGraph)
                .addProperty(
                    PropertySpec.builder(
                        "startDestination",
                        KClass::class.asTypeName().parameterizedBy(STAR),
                        KModifier.OVERRIDE
                    )
                        .initializer("%L::class", startDestination.toClassName().canonicalName)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder(
                        "route",
                        KClass::class.asTypeName().parameterizedBy(STAR),
                        KModifier.OVERRIDE
                    )
                        .initializer("%L::class", route.toClassName().canonicalName)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder(
                        "typeMap",
                        Map::class.asClassName().parameterizedBy(
                            KType::class.asTypeName(),
                            ClassName("androidx.navigation", "NavType").parameterizedBy(STAR)
                        ),
                        KModifier.OVERRIDE
                    ).apply {
                        if (route.isObjectClass) {
                            initializer("emptyMap()")
                        } else {
                            initializer(
                                "%L.typeMap()",
                                route.declaration.qualifiedName!!.asString()
                            )
                        }
                    }.build()
                )
                .addProperty(
                    PropertySpec.builder(
                        "screenDestinations",
                        List::class.asClassName().parameterizedBy(
                            ScreenDestination
                        ),
                        KModifier.OVERRIDE
                    )
                        .addKdoc("Retrieved from [${(includeObject.parent as KSClassDeclaration).simpleName.asString()}.${includeObject.simpleName.asString()}]")
                        .apply {
                            if (inGraphRoute.isEmpty()) {
                                initializer("emptyList()")
                            } else {

                                initializer(
                                    "listOf(%L)",
                                    inGraphRoute.joinToString(",") { route ->
                                        val genFunctionName =
                                            route.toClassName().simpleName + "Destination"
                                        "${route.declaration.packageName.asString()}.$genFunctionName()"
                                    }
                                )
                            }
                        }.build()
                )
                .addProperty(
                    PropertySpec.builder(
                        "nestedNavGraphs",
                        List::class.asClassName().parameterizedBy(NavGraph),
                        KModifier.OVERRIDE
                    ).apply {
                        if (nestedNavGraphRoute.isEmpty()) {
                            initializer("emptyList()")
                        } else {
                            initializer(
                                "listOf(%L)",
                                nestedNavGraphRoute.joinToString(",") { graph ->
                                    val className = graph.toClassName().simpleName + "NavGraph"
                                    "${graph.declaration.packageName.asString()}.$className()"
                                }
                            )
                        }
                    }.build()
                )
                .build()
            val rootPackage =
                if (isRoot) root.declaration.packageName.asString() else route.declaration.packageName.asString()
            FileSpec.builder(rootPackage, "$className.desktop")
                .indent("    ")
                .addKotlinDefaultImports()
                .addImport(ScreenDestination, "")
                .addImport(KoinScope, "")
                .addImport(route.toClassName(), "")
                .addType(clazz)
                .build()
                .writeTo(codeGenerator, Dependencies(true))
        }
    }
    return skipSymbols
}

fun generateTypeMap(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
    routeType: KSClassDeclaration,
) {
    logger.info("GenerateTypeMap... ${routeType.qualifiedName?.asString()}")
    val className = routeType.simpleName.asString()
    val packageName = routeType.packageName.asString()
    val types = routeType.primaryConstructor?.parameters?.mapNotNull {
        val ksType = it.type.resolve()
        when (ksType.makeNotNullable().toTypeName()) {
            Int::class.asTypeName() -> ksType to "NavType.IntType"
            IntArray::class.asTypeName() -> ksType to "NavType.IntArrayType"
            Long::class.asTypeName() -> ksType to "NavType.LongType"
            LongArray::class.asTypeName() -> ksType to "NavType.LongArrayType"
            Float::class.asTypeName() -> ksType to "NavType.FloatType"
            FloatArray::class.asTypeName() -> ksType to "NavType.FloatArrayType"
            Boolean::class.asTypeName() -> ksType to "NavType.BoolType"
            BooleanArray::class.asTypeName() -> ksType to "NavType.BoolArrayType"
            String::class.asTypeName() -> ksType to "NavType.StringType"
            Array::class.asTypeName()
                .parameterizedBy(String::class.asTypeName()),
            -> ksType to "NavType.StringArrayType"

            List::class.asTypeName()
                .parameterizedBy(Int::class.asTypeName()),
            -> ksType to "NavType.IntListType"

            List::class.asTypeName()
                .parameterizedBy(Long::class.asTypeName()),
            -> ksType to "NavType.LongListType"

            List::class.asTypeName()
                .parameterizedBy(Float::class.asTypeName()),
            -> ksType to "NavType.FloatListType"

            List::class.asTypeName()
                .parameterizedBy(Boolean::class.asTypeName()),
            -> ksType to "NavType.BoolListType"

            List::class.asTypeName()
                .parameterizedBy(String::class.asTypeName()),
            -> ksType to "NavType.StringListType"

            else -> {
                if (ksType.declaration.annotations.any {
                        it.annotationType.resolve().toClassName() == Serializable
                    }) {
                    ksType to "NavType.kSerializableType<${
                        it.type.resolve().toClassName().canonicalName
                    }>()"
                } else {
                    logger.warn("$ksType is not support")
                    ksType to "NavType.kSerializableType<${
                        it.type.resolve().toClassName().canonicalName
                    }>()"
                }
            }
        }

    } ?: return
    val func = FunSpec.builder("typeMap")
        // Route.
        .receiver(routeType.toClassName().nestedClass("Companion"))
        .returns(
            Map::class.asClassName().parameterizedBy(
                KType::class.asClassName(),
                ClassName("androidx.navigation", "NavType")
                    .parameterizedBy(STAR)
            ),
        )
        .addStatement(
            "return mapOf(\n%L\n)",
            types.joinToString(",\n") { "    typeOf<${it.first.toClassNameOrNull()?.simpleName ?: it.first}>() to ${it.second}" })
        .build()

    FileSpec.builder(packageName, "$className.nav")
        .addImport("kotlin.reflect", "typeOf")
        .addImport("androidx.navigation", "NavType")
        .apply {
            types.forEach {
                addImport(
                    it.first.declaration.packageName.asString(),
                    it.first.declaration.simpleName.asString()
                )
            }
        }
        .addImport(
            "com.sorrowblue.comicviewer.framework.navigation",
            "kSerializableType"
        )
        .addFunction(func)
        .build()
        .writeTo(codeGenerator, Dependencies(true))
}

class BuilderProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {

    init {
        logger.warn("##### BuilderProcessor init #####")
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("Scan symbols ... ${resolver.getModuleName().asString()}")
        val list = DestinationResolver(codeGenerator, logger, resolver).toList()
        return list + NavGraphResolver(codeGenerator, logger, resolver).also {
            if (it.isNotEmpty()) {
                logger.warn(
                    "skip class ${
                        it.joinToString(", ") {
                            (it as? KSClassDeclaration)?.qualifiedName?.asString().orEmpty()
                        }
                    }"
                )
            }
        }
    }
}
