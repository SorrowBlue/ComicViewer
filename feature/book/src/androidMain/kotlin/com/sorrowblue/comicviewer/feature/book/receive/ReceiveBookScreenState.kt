package com.sorrowblue.comicviewer.feature.book.receive

/*
internal interface ReceiveBookScreenState : SaveableScreenState {

    val uiState: BookScreenUiState
    val currentList: SnapshotStateList<PageItem>
    val pagerState: PagerState
    val systemUiController: SystemUiController

    fun toggleTooltip()
    fun onPageChange(page: Int)
    fun onPageLoaded(split: UnratedPage, bitmap: Bitmap)
}

@KoinViewModel
internal class ReceiveBookViewModel(val getIntentBookUseCase: GetIntentBookUseCase) :
    ViewModel()

@Composable
internal fun rememberReceiveBookScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    currentList: SnapshotStateList<PageItem> = remember { mutableStateListOf() },
    pagerState: PagerState = rememberPagerState(initialPage = 0, pageCount = { currentList.size }),
    systemUiController: SystemUiController = rememberSystemUiController(),
    viewModel: ReceiveBookViewModel = koinViewModel(),
): ReceiveBookScreenState {
    return rememberSaveableScreenState {
        ReceiveBookScreenStateImpl(
            context = context,
            scope = scope,
            pagerState = pagerState,
            systemUiController = systemUiController,
            currentList = currentList,
            savedStateHandle = it,
            getIntentBookUseCase = viewModel.getIntentBookUseCase
        )
    }
}

private class ReceiveBookScreenStateImpl(
    context: Context,
    private val scope: CoroutineScope,
    override val pagerState: PagerState,
    override val systemUiController: SystemUiController,
    override val currentList: SnapshotStateList<PageItem>,
    override val savedStateHandle: SavedStateHandle,
    private val getIntentBookUseCase: GetIntentBookUseCase,
) : ReceiveBookScreenState {
    init {
        val activity = context as Activity
        val data = activity.intent.data
        if (data == null) {
            Toast.makeText(context, "ファイルを開けませんでした", Toast.LENGTH_SHORT).show()
        } else {
            scope.launch {
                getIntentBookUseCase(GetIntentBookUseCase.Request(data.toString())).collect { resource ->
                    when (resource) {
                        is Resource.Error -> Unit
                        is Resource.Success -> {
                            logcat { "book=${resource.data}" }
                            uiState = BookScreenUiState.Loaded(
                                resource.data,
                                FavoriteId(),
                                BookSheetUiState(resource.data)
                            )
                            currentList.clear()
                            currentList.addAll(
                                buildList {
                                    addAll(
                                        (1..resource.data.totalPageCount).map {
                                            BookPage.Default(it - 1)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(""))
        private set

    override fun toggleTooltip() {
        if (uiState !is BookScreenUiState.Loaded) return
        uiState =
            (uiState as BookScreenUiState.Loaded).copy(isVisibleTooltip = !systemUiController.isSystemBarsVisible)
        systemUiController.isSystemBarsVisible = !systemUiController.isSystemBarsVisible
    }

    override fun onPageChange(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page)
        }
    }

    override fun onPageLoaded(split: UnratedPage, bitmap: Bitmap) {
        when (split) {
            is BookPage.Spread.Unrated -> {
                onPageLoaded2(split, bitmap)
            }

            is BookPage.Split.Unrated -> {
            }
        }
    }

    private fun onPageLoaded2(spread: BookPage.Spread.Unrated, bitmap: Bitmap) {
        val index = currentList.indexOf(spread)
        if (bitmap.width < bitmap.height) {
            currentList[index] = BookPage.Spread.Single(spread.index)
        } else {
            // 横
            currentList[index] = BookPage.Spread.Spread2(spread.index)
        }

        val skipIndex = mutableListOf<Int>()
        val newList = mutableListOf<PageItem>()
        var nextSingle: BookPage.Spread.Single? = null
        currentList.forEachIndexed { index1, bookItem ->
            if (skipIndex.contains(index1)) return@forEachIndexed
            when (val item = nextSingle ?: bookItem) {
                is BookPage.Spread.Combine -> newList.add(item)
                is BookPage.Spread.Single -> {
                    if (item.index == 0) {
                        newList.add(item)
                        nextSingle = null
                    } else {
                        when (val nextItem = currentList[index1 + 1]) {
                            is BookPage.Spread.Single -> {
                                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                                skipIndex += index1 + 1
                                nextSingle = null
                            }

                            is BookPage.Spread.Combine -> {
                                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                                nextSingle = BookPage.Spread.Single(nextItem.nextIndex)
                            }

                            else -> {
                                newList.add(item)
                                nextSingle = null
                            }
                        }
                    }
                }

                is BookPage.Spread.Spread2 -> newList.add(item)
                is BookPage.Spread.Unrated -> newList.add(item)
                else -> newList.add(item)
            }
        }
        currentList.clear()
        currentList.addAll(newList)
    }
}
*/
