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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.editor.basic.section.BasicCollectionContent
import com.sorrowblue.comicviewer.feature.collection.editor.component.OutlinedTextField
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.kSerializableSaver
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_basic_edit
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import soil.form.FormPolicy
import soil.form.compose.Controller
import soil.form.compose.FieldControl
import soil.form.compose.Form
import soil.form.compose.FormScope
import soil.form.compose.rememberFieldRuleControl
import soil.form.compose.rememberSubmissionRuleAutoControl
import soil.form.rule.notBlank

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
    onSubmit: suspend (BasicCollectionEditorFormData) -> Unit,
    onBackClick: () -> Unit,
    onDeleteClick: (File) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
) {
    Form(
        onSubmit = onSubmit,
        saver = kSerializableSaver<BasicCollectionEditorFormData>(),
        key = uiState.formData.name,
        initialValue = uiState.formData,
        policy = FormPolicy.Default
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text(stringResource(Res.string.collection_editor_title_basic_edit)) },
                        navigationIcon = { BackIconButton(onClick = onBackClick) },
                        actions = {
                            Controller(rememberSubmissionRuleAutoControl()) {
                                IconButton(onClick = it.onSubmit, enabled = it.canSubmit) {
                                    Icon(ComicIcons.Save, null)
                                }
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
                OutlinedTextField(
                    control = rememberCollectionNameControl(),
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
}

@Composable
private fun FormScope<BasicCollectionEditorFormData>.rememberCollectionNameControl(): FieldControl<String> {
    return rememberFieldRuleControl(
        name = "Collection name",
        select = { this.name },
        update = { copy(name = it) },
    ) {
        notBlank { "must not be blank" }
    }
}
