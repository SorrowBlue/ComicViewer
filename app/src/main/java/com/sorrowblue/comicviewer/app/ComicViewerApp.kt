package com.sorrowblue.comicviewer.app

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.sorrowblue.comicviewer.bookshelf.navigation.navigateToBookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.authentication.navigation.navigateToAuthentication
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialRoute
import com.sorrowblue.comicviewer.feature.tutorial.navigation.navigateToTutorial
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.compactDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.expandedDimension
import com.sorrowblue.comicviewer.framework.designsystem.theme.mediumDimension
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.lifecycle.LaunchedEffectUiEvent
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile
import kotlin.math.sqrt
import logcat.LogPriority
import logcat.logcat

internal sealed interface ComicViewerAppUiEvent {

    data class StartTutorial(val done: () -> Unit) : ComicViewerAppUiEvent

    data class CompleteTutorial(val isFirstTime: Boolean) : ComicViewerAppUiEvent

    class RestoreHistory(val history: NavigationHistory) : ComicViewerAppUiEvent

    data class RequireAuthentication(val isRestoredNavHistory: Boolean, val done: () -> Unit) :
        ComicViewerAppUiEvent
}

fun Modifier.drawDiagonalLabel(
    text: String,
    color: Color,
    style: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    ),
    labelTextRatio: Float = 7f,
) = composed(
    factory = {

        val textMeasurer = rememberTextMeasurer()
        val textLayoutResult: TextLayoutResult = remember {
            textMeasurer.measure(text = AnnotatedString(text), style = style)
        }


        Modifier
            .clipToBounds()
            .drawWithContent {
                val canvasWidth = size.width

                val textSize = textLayoutResult.size
                val textWidth = textSize.width
                val textHeight = textSize.height

                val rectWidth = textWidth * labelTextRatio
                val rectHeight = textHeight * 1.1f

                val rect = Rect(
                    offset = Offset(canvasWidth - rectWidth, 0f),
                    size = Size(rectWidth, rectHeight)
                )

                val sqrt = sqrt(rectWidth / 2f)
                val translatePos = sqrt * sqrt

                drawContent()
                withTransform(
                    {
                        rotate(
                            degrees = 45f,
                            pivot = Offset(
                                canvasWidth - rectWidth / 2,
                                translatePos
                            )
                        )
                    }
                ) {
                    drawRect(
                        color = color,
                        topLeft = rect.topLeft,
                        size = rect.size
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = text,
                        style = style,
                        topLeft = Offset(
                            rect.left + (rectWidth - textWidth) / 2f,
                            rect.top + (rect.bottom - textHeight) / 2f
                        )
                    )
                }

            }
    }
)

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun ComicViewerApp(
    windowsSize: WindowSizeClass,
    viewModel: ComicViewerAppViewModel,
) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val graphStateHolder = rememberGraphStateHolder()

    val dimension = when (windowsSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> compactDimension
        WindowWidthSizeClass.Medium -> mediumDimension
        WindowWidthSizeClass.Expanded -> expandedDimension
        else -> compactDimension
    }
    CompositionLocalProvider(
        LocalWindowSize provides windowsSize,
        LocalDimension provides dimension,
    ) {
        ComicTheme {
            val addOnList by viewModel.addOnList.collectAsState()
            val activity = LocalContext.current as Activity
            val isMobile = rememberMobile()
            MainScreen(
                bottomSheetNavigator = bottomSheetNavigator,
                navController = navController,
                startDestination = graphStateHolder.startDestination,
                routeToTab = graphStateHolder::routeToTab,
                routeToFab = graphStateHolder::routeToFab,
                onTabSelected = graphStateHolder::onTabSelected,
                onFabClick = graphStateHolder::onTabClick,
            ) { navHostController, contentPadding ->
                mainGraph(
                    isMobile = isMobile,
                    navController = navHostController,
                    contentPadding = contentPadding,
                    restoreComplete = viewModel::completeRestoreHistory,
                    onTutorialExit = viewModel::onCompleteTutorial,
                    onBackClick = {
                        ActivityCompat.finishAffinity(activity)
                    },
                    onAuthCompleted = { handleBack ->
                        if (handleBack) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(
                                graphStateHolder.startDestination,
                                navOptions {
                                    popUpTo(com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationRoute) {
                                        inclusive = true
                                    }
                                }
                            )
                        }
                    },
                    addOnList = addOnList,
                )
            }
            if (BuildConfig.BUILD_TYPE != "release") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawDiagonalLabel(
                            text = BuildConfig.BUILD_TYPE.uppercase(),
                            color = ComicTheme.colorScheme.tertiaryContainer.copy(alpha = 0.75f),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ComicTheme.colorScheme.onTertiaryContainer
                            )
                        )
                )
            }
        }
    }
    LaunchedEffectUiEvent(viewModel.uiEvents, viewModel::consumeUiEvent) { uiEvent ->
        when (uiEvent) {
            is ComicViewerAppUiEvent.StartTutorial -> {
                navController.navigateToTutorial(navOptions {
                    popUpTo(mainGraphRoute) {
                        inclusive = true
                    }
                })
                uiEvent.done()
            }

            is ComicViewerAppUiEvent.CompleteTutorial ->
                if (uiEvent.isFirstTime) {
                    navController.navigate(
                        graphStateHolder.startDestination,
                        navOptions {
                            popUpTo(TutorialRoute) {
                                inclusive = true
                            }
                        }
                    )
                } else {
                    navController.popBackStack()
                }

            is ComicViewerAppUiEvent.RestoreHistory -> {
                val (bookshelf, folderList, position) = uiEvent.history.triple
                if (folderList.isEmpty()) {
                    viewModel.completeRestoreHistory()
                } else if (folderList.size == 1) {
                    navController.navigateToBookshelfFolder(
                        bookshelf.id,
                        folderList.first().path,
                        position
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelf.id}) -> folder(${folderList.first().path})"
                    }
                } else {
                    navController.navigateToBookshelfFolder(
                        bookshelf.id,
                        folderList.first().path
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "bookshelf(${bookshelf.id}) -> folder(${folderList.first().path})"
                    }
                    folderList.drop(1).dropLast(1).forEach { folder ->
                        navController.navigateToBookshelfFolder(bookshelf.id, folder.path)
                        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                            "-> folder(${folder.path})"
                        }
                    }
                    navController.navigateToBookshelfFolder(
                        bookshelf.id,
                        folderList.last().path,
                        position
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "-> folder${folderList.last().path}, $position"
                    }
                }
            }

            is ComicViewerAppUiEvent.RequireAuthentication -> {
                if (uiEvent.isRestoredNavHistory) {
                    navController.navigateToAuthentication(
                        Mode.Authentication,
                        true,
                        navOptions { launchSingleTop = true }
                    )
                } else {
                    navController.navigateToAuthentication(
                        Mode.Authentication,
                        false,
                        navOptions {
                            launchSingleTop = true
                            popUpTo(mainGraphRoute) {
                                inclusive = true
                            }
                        }
                    )
                }
                uiEvent.done()
            }

        }
    }
    LifecycleEffect(Lifecycle.Event.ON_CREATE, action = viewModel::onCreate)
    LifecycleEffect(Lifecycle.Event.ON_START, action = viewModel::onStart)
}
