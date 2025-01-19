package com.sorrowblue.comicviewer.feature.bookshelf.navigation

import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.annotation.NavGraph

@NavGraph<ExternalModuleGraph>(defaultTransitions = BookshelfGraphTransitions::class)
internal annotation class BookshelfGraph
