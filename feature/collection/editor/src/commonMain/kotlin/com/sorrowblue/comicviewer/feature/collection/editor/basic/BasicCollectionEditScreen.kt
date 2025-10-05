package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.editor.basic.section.BasicCollectionContent
import com.sorrowblue.comicviewer.feature.collection.editor.component.CollectionNameTextField
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_basic_edit
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import soil.form.compose.rememberForm
import soil.form.compose.rememberFormState

@Serializable
internal data class BasicCollectionEdit(val id: CollectionId)

@Destination<BasicCollectionEdit>
@Composable
internal fun BasicCollectionEditScreen(
    route: BasicCollectionEdit,
    navController: NavController = koinInject(),
) {
    val state = rememberBasicCollectionEditScreenState(route)

    BasicCollectionEditScreen(
        uiState = state.uiState,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        onSubmit = state::onSubmit,
        onBackClick = navController::navigateUp,
        onDeleteClick = state::onDeleteClick,
    )
}

@Serializable
internal data class BasicCollectionEditScreenUiState(
    val formData: BasicCollectionEditorFormData = BasicCollectionEditorFormData(),
)

@Composable
private fun BasicCollectionEditScreen(
    uiState: BasicCollectionEditScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onSubmit: (BasicCollectionEditorFormData) -> Unit,
    onBackClick: () -> Unit,
    onDeleteClick: (File) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    val formState = rememberFormState(
        initialValue = uiState.formData,
        saver = kSerializableSaver<BasicCollectionEditorFormData>(),
    )
    val form = rememberForm(state = formState, onSubmit = onSubmit)
    var name by remember { mutableStateOf(uiState.formData.name) }
    LaunchedEffect(uiState.formData.name) {
        if (name != uiState.formData.name) {
            name = uiState.formData.name
            formState.reset(uiState.formData)
        }
    }
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(Res.string.collection_editor_title_basic_edit)) },
                    navigationIcon = { BackIconButton(onClick = onBackClick) },
                    actions = {
                        IconButton(onClick = form::handleSubmit, enabled = form.meta.canSubmit) {
                            Icon(ComicIcons.Save, null)
                        }
                    },
                    windowInsets = WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(modifier = Modifier.padding(top = contentPadding.calculateTopPadding())) {
            CollectionNameTextField(
                form = form,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ComicTheme.dimension.margin),
            )
            BasicCollectionContent(
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding.only(PaddingValuesSides.Horizontal + PaddingValuesSides.Bottom),
                onDeleteClick = onDeleteClick
            )
        }
    }
}
