/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreen
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreen
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenUiState
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionForm
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionsCreateScreenUiState
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditorScreen
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditorScreenUiState
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CollectionNameField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.QueryField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.test.MediumTest
import com.sorrowblue.comicviewer.framework.ui.form.FormState
import com.sorrowblue.comicviewer.framework.ui.form.rememberForm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.flowOf

@MediumTest
@OptIn(ExperimentalTestApi::class)
class CollectionEditorFormUiTest {

    @Test
    fun basicCollectionCreateScreen_validatesNameAndSubmits() = runComposeUiTest {
        var submittedForm: BasicCollectionForm? = null
        val formState = FormState(BasicCollectionForm())

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = { submittedForm = it })
                BasicCollectionCreateScreen(
                    uiState = BasicCollectionsCreateScreenUiState(isLoading = false),
                    form = form,
                    onDismissRequest = {},
                )
            }
        }

        onNodeWithTag("CreateButton").assertIsNotEnabled()

        onNodeWithTag(CollectionNameField).performTextInput("My Collection")
        onNodeWithTag("CreateButton").assertIsEnabled()

        onNodeWithTag(CollectionNameField).performTextClearance()
        onNodeWithTag("CreateButton").assertIsNotEnabled()

        onNodeWithTag(CollectionNameField).performTextInput("My Collection")
        onNodeWithTag("CreateButton").assertIsEnabled()
        onNodeWithTag("CreateButton").performClick()

        assertEquals(BasicCollectionForm(name = "My Collection"), submittedForm)
    }

    @Test
    fun basicCollectionEditScreen_resetAndValidation() = runComposeUiTest {
        var submittedForm: BasicCollectionForm? = null
        val formState = FormState(BasicCollectionForm())

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = { submittedForm = it })
                val lazyPagingItems = flowOf(PagingData.empty<File>()).collectAsLazyPagingItems()
                BasicCollectionEditScreen(
                    uiState = BasicCollectionEditScreenUiState(isLoading = false),
                    form = form,
                    lazyPagingItems = lazyPagingItems,
                    onBackClick = {},
                    onDeleteClick = {},
                )
            }
        }

        onNodeWithTag("CreateButton").assertIsNotEnabled()

        runOnIdle {
            formState.reset(BasicCollectionForm(name = "Existing Collection"))
        }
        onNodeWithTag(CollectionNameField).assertTextContains("Existing Collection")
        onNodeWithTag("CreateButton").assertIsEnabled()

        onNodeWithTag(CollectionNameField).performTextClearance()
        onNodeWithTag("CreateButton").assertIsNotEnabled()

        onNodeWithTag(CollectionNameField).performTextInput("Updated Collection")
        onNodeWithTag("CreateButton").assertIsEnabled()
        onNodeWithTag("CreateButton").performClick()

        assertEquals(BasicCollectionForm(name = "Updated Collection"), submittedForm)
    }

    @Test
    fun smartCollectionEditorScreen_validatesNameAndQueryAndSubmits() = runComposeUiTest {
        var submittedForm: SmartCollectionForm? = null
        val formState = FormState(SmartCollectionForm())

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = { submittedForm = it })
                SmartCollectionEditorScreen(
                    form = form,
                    uiState = SmartCollectionEditorScreenUiState(
                        enabledForm = true,
                        bookshelf = mapOf(null to "All Bookshelves"),
                    ),
                    title = { Text("Smart Collection") },
                    onCancel = {},
                )
            }
        }

        // Initially disabled because both CollectionNameField and QueryField are blank
        onNodeWithTag("CreateButton").assertIsNotEnabled()

        // Filling CollectionNameField alone keeps CreateButton disabled while QueryField is blank
        onNodeWithTag(CollectionNameField).performTextInput("Favorites")
        onNodeWithTag("CreateButton").assertIsNotEnabled()

        // Filling QueryField enables CreateButton
        onNodeWithTag(QueryField).performTextInput("comic")
        onNodeWithTag("CreateButton").assertIsEnabled()

        // Clearing QueryField disables CreateButton
        onNodeWithTag(QueryField).performTextClearance()
        onNodeWithTag("CreateButton").assertIsNotEnabled()

        // Re-entering QueryField and clicking CreateButton submits the form
        onNodeWithTag(QueryField).performTextInput("manga")
        onNodeWithTag("CreateButton").assertIsEnabled()
        onNodeWithTag("CreateButton").performClick()

        assertEquals("Favorites", submittedForm?.name)
        assertEquals("manga", submittedForm?.searchCondition?.query)
    }
}
