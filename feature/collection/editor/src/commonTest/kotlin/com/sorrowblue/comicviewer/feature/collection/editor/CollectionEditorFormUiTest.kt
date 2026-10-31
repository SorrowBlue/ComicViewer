/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreen
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionForm
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionsCreateScreenUiState
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditorScreenUiState
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CollectionNameField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.CreateButton
import com.sorrowblue.comicviewer.feature.collection.editor.smart.component.QueryField
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionEditorForm
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.test.MediumTest
import com.sorrowblue.comicviewer.framework.ui.form.FormState
import com.sorrowblue.comicviewer.framework.ui.form.rememberForm
import kotlin.test.Test
import kotlin.test.assertEquals

@MediumTest
class CollectionEditorFormUiTest {

    @Test
    fun basicCollectionCreateScreen_validatesResetAndSubmits() = runComposeUiTest {
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

        // Resetting formState updates CollectionNameField and keeps validation active
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
    fun smartCollectionEditorForm_validatesNameAndQueryAndSubmits() = runComposeUiTest {
        var submittedForm: SmartCollectionForm? = null
        val formState = FormState(SmartCollectionForm())

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = { submittedForm = it })
                val uiState = SmartCollectionEditorScreenUiState(
                    enabledForm = true,
                    bookshelf = mapOf(null to "All Bookshelves"),
                )
                Column {
                    SmartCollectionEditorForm(
                        form = form,
                        uiState = uiState,
                        bookshelf = uiState.bookshelf,
                    )
                    CreateButton(
                        form = form,
                        modifier = Modifier.testTag("CreateButton"),
                    )
                }
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
