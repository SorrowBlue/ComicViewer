package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeBottom
import com.sorrowblue.comicviewer.framework.designsystem.theme.largeTop
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.debugPlaceholder
import com.sorrowblue.comicviewer.framework.ui.material3.ReversePermanentNavigationDrawer

@Composable
fun rememberMobile(): Boolean {
    val windowSize = LocalWindowSize.current
    return remember(windowSize) {
        windowSize.widthSizeClass == WindowWidthSizeClass.Compact
    }
}

@Composable
fun FullScreen(paddingValues: PaddingValues, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ComicTheme.colorScheme.tertiaryContainer)
            .padding(paddingValues)
    ) {

        Text(
            text = "TopStart",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(100.dp)
                .background(Color.Green)
                .align(Alignment.TopStart)
        )
        Text(
            text = "TopEnd",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(100.dp)
                .background(Color.Red)
                .align(Alignment.TopEnd)
        )
        Button(onClick = onBack) {
            Text(text = "FullScreen")
        }
        Text(
            text = "BottomStart",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(100.dp)
                .background(Color.Blue)
                .align(Alignment.BottomStart)
        )
        Text(
            text = "BottomEnd",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(100.dp)
                .background(Color.Yellow)
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun Basic(paddingValues: PaddingValues, onBack: () -> Unit) {
    Surface(
        shape = ComicTheme.shapes.large,
        color = if (rememberMobile()) ComicTheme.colorScheme.surfaceVariant else ComicTheme.colorScheme.surface,
        modifier = Modifier.padding(paddingValues)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Button(onClick = onBack, Modifier.align(Alignment.Center)) {
                Text(text = "Back")
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithAppbar(paddingValues: PaddingValues, onBack: () -> Unit) {
    val isMobile = rememberMobile()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            if (isMobile) {
                TopAppBar(
                    title = { Text(text = "WithAppbar") },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(),
                    scrollBehavior = scrollBehavior,
                )
            } else {
                TopAppBar(
                    title = {
                        Text(text = "WithAppbar")
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ComicTheme.colorScheme.surfaceContainerLowest
                    ),
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Top + WindowInsetsSides.End
                            )
                        )
                        .clip(ComicTheme.shapes.large)
                )
            }
        },
        contentWindowInsets = paddingValues.asWindowInsets(),
        containerColor = if (isMobile) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Surface(
            shape = ComicTheme.shapes.large,
            color = if (rememberMobile()) ComicTheme.colorScheme.surfaceVariant else ComicTheme.colorScheme.surface,
            modifier = Modifier.padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(onClick = onBack, Modifier.align(Alignment.Center)) {
                    Text(text = "Back")
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithAppbarList(paddingValues: PaddingValues, onBack: () -> Unit) {
    val isMobile = rememberMobile()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            if (isMobile) {
                TopAppBar(
                    title = { Text(text = "WithAppbarList") },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(),
                    scrollBehavior = scrollBehavior,
                )
            } else {
                TopAppBar(
                    title = {
                        Text(text = "WithAppbarList")
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ComicTheme.colorScheme.surfaceContainerLowest
                    ),
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Top + WindowInsetsSides.End
                            )
                        )
                        .padding(end = ComicTheme.dimension.margin)
                        .clip(ComicTheme.shapes.large)
                )
            }
        },
        contentWindowInsets = paddingValues.asWindowInsets(),
        containerColor = if (isMobile) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (isMobile) {
            LazyColumn(
                contentPadding = innerPadding.add(
                    PaddingValues(
                        bottom = if (true /*showfab*/) 16.dp + 56.dp else 0.dp
                    )
                ),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(20) {
                    Text(
                        text = "WithAppbarList$it",
                        style = ComicTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ComicTheme.colorScheme.surface)
                            .padding(16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding.add(
                    paddingValues = PaddingValues(
                        top = ComicTheme.dimension.spacer,
                        bottom = ComicTheme.dimension.margin,
                        end = ComicTheme.dimension.margin
                    )
                ),
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                items(20) {
                    Text(
                        text = "WithAppbarList$it",
                        style = ComicTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                when (it) {
                                    0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                                    19 -> Modifier.clip(ComicTheme.shapes.largeBottom)
                                    else -> Modifier
                                }
                            )
                            .background(ComicTheme.colorScheme.surface)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Default(paddingValues: PaddingValues, onBack: () -> Unit) {
    val isMobile = rememberMobile()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Default Title")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isMobile) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainerLowest
                ),
                windowInsets = if (isMobile) TopAppBarDefaults.windowInsets else WindowInsets(0),
                scrollBehavior = scrollBehavior,
                modifier = if (isMobile) Modifier else {
                    Modifier
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                }
            )
        },
        contentWindowInsets = paddingValues.asWindowInsets(),
        containerColor = if (isMobile) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(160.dp),
            contentPadding = it.add(
                PaddingValues(
                    start = ComicTheme.dimension.margin,
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin + if (isMobile && true /*showfab*/) 16.dp + 56.dp else 0.dp
                )
            ),
            verticalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding * 2),
            horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding * 2)
        ) {
            items(8) {
                Card(
                    colors = CardDefaults.cardColors(
                        if (isMobile) ComicTheme.colorScheme.surfaceVariant else ComicTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = "https://picsum.photos/200?index=$it",
                            contentDescription = null,
                            placeholder = debugPlaceholder(),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(ComicTheme.shapes.large)
                        )
                        Text(
                            text = "Lorem ipsum dolor sit amet ${it + 1}",
                            style = ComicTheme.typography.titleMedium,
                            modifier = Modifier.padding(ComicTheme.dimension.padding * 2)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppbarListSide(paddingValues: PaddingValues, onBack: () -> Unit) {
    val isMobile = rememberMobile()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            if (isMobile) {
                TopAppBar(
                    title = { Text(text = "WithAppbarList") },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(),
                    scrollBehavior = scrollBehavior,
                )
            } else {
                TopAppBar(
                    title = {
                        Text(text = "WithAppbarList")
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Top + WindowInsetsSides.End
                            )
                        )
                        .padding(horizontal = ComicTheme.dimension.margin)
                        .padding(end = ComicTheme.dimension.margin)
                        .clip(CircleShape),
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        contentWindowInsets = paddingValues.asWindowInsets(),
        containerColor = if (isMobile) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        var isVisibleSideSheet by remember { mutableStateOf(false) }
        ReversePermanentNavigationDrawer(drawerContent = {
            SideSheet(!isMobile && isVisibleSideSheet) {
                if (it) {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding.copy(bottom = 0.dp))
                            .padding(
                                top = ComicTheme.dimension.spacer,
                                start = ComicTheme.dimension.spacer,
                                end = ComicTheme.dimension.margin
                            )
                            .width(256.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(ComicTheme.shapes.largeTop)
                                .background(ComicTheme.colorScheme.surface)
                                .padding(start = 24.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Title",
                                style = ComicTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .weight(1f)
                            )
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = ComicIcons.Close, contentDescription = null)
                            }
                        }
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = innerPadding.calculateBottomPadding() + ComicTheme.dimension.margin)
                                .clip(ComicTheme.shapes.largeBottom)
                        ) {
                            repeat(7) {
                                Text(
                                    text = "WithAppbarList$it",
                                    style = ComicTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ComicTheme.colorScheme.surface)
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.fillMaxHeight())
                }
            }
        }) {
            val end by animateDpAsState(
                targetValue = if (isVisibleSideSheet) {
                    ComicTheme.dimension.spacer
                } else {
                    innerPadding.calculateEndPadding(LocalLayoutDirection.current) + ComicTheme.dimension.margin
                }, label = "end"
            )
            if (isMobile) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    contentPadding = innerPadding
                        .add(PaddingValues(bottom = ComicTheme.dimension.margin + 56.dp + 16.dp))
                        .add(PaddingValues(horizontal = ComicTheme.dimension.margin)),
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(20) {
                        Card {
                            Column {
                                AsyncImage(
                                    model = "https://picsum.photos/200?index=$it",
                                    contentDescription = null,
                                    placeholder = debugPlaceholder(),
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(
                                            RoundedCornerShape(
                                                bottomStart = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        ),
                                    contentScale = ContentScale.Crop,
                                )
                                Text(
                                    text = "WithAppbarList$it",
                                    style = ComicTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            when (it) {
                                                0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                                                19 -> Modifier.clip(ComicTheme.shapes.largeBottom)
                                                else -> Modifier
                                            }
                                        )
                                        .clickable { isVisibleSideSheet = !isVisibleSideSheet }
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    contentPadding = innerPadding
                        .copy(end = end)
                        .add(
                            paddingValues = PaddingValues(
                                top = ComicTheme.dimension.spacer,
                                bottom = ComicTheme.dimension.margin,
                            )
                        ),
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(20) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = ComicTheme.colorScheme.surface
                            )
                        ) {
                            Column {
                                AsyncImage(
                                    model = "https://picsum.photos/200?index=$it",
                                    contentDescription = null,
                                    placeholder = debugPlaceholder(),
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(
                                            RoundedCornerShape(
                                                bottomStart = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        ),
                                    contentScale = ContentScale.Crop,
                                )
                                Text(
                                    text = "WithAppbarList$it",
                                    style = ComicTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            when (it) {
                                                0 -> Modifier.clip(ComicTheme.shapes.largeTop)
                                                19 -> Modifier.clip(ComicTheme.shapes.largeBottom)
                                                else -> Modifier
                                            }
                                        )
                                        .clickable { isVisibleSideSheet = !isVisibleSideSheet }
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideSheet(isShow: Boolean, content: @Composable (Boolean) -> Unit) {
    AnimatedContent(
        targetState = isShow,
        label = "drawerContent",
        contentAlignment = Alignment.CenterStart,
        transitionSpec = {
            expandHorizontally(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium4,
                    delayMillis = 0,
                    easing = MotionTokens.EasingEmphaizedDecelerateInterpolator
                ), expandFrom = Alignment.Start
            ) togetherWith shrinkHorizontally(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationMedium1,
                    delayMillis = 0,
                    easing = MotionTokens.EasingEmphasizedAccelerateInterpolator
                ), shrinkTowards = Alignment.Start
            )
        }
    ) { visible ->
        content(visible)
    }
}
