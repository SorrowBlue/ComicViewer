/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.DisplayNameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldName
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.FolderSelectFieldState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.HostField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PasswordField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PathFieldName
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.PortField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.UsernameField
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.BookshelfEditorScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.DeviceEditorForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.editor.SmbEditorForm
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceEditorContents
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditorContents
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.test.MediumTest
import com.sorrowblue.comicviewer.framework.ui.form.FieldError
import com.sorrowblue.comicviewer.framework.ui.form.FieldValidator
import com.sorrowblue.comicviewer.framework.ui.form.FormField
import com.sorrowblue.comicviewer.framework.ui.form.FormState
import com.sorrowblue.comicviewer.framework.ui.form.notNull
import com.sorrowblue.comicviewer.framework.ui.form.rememberField
import com.sorrowblue.comicviewer.framework.ui.form.rememberForm
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@MediumTest
@OptIn(ExperimentalTestApi::class)
class BookshelfEditorFormUiTest {

    @Test
    fun smbEditorContents_guestAuth_allowsEmptyPathAndValidatesRequiredFields() = runComposeUiTest {
        var submittedForm: SmbEditorForm? = null
        val formState = FormState(SmbEditorForm())

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = { submittedForm = it })
                Column {
                    SmbEditorContents(
                        form = form,
                        uiState = BookshelfEditorScreenUiState(progress = false),
                    )
                    Button(
                        onClick = form::handleSubmit,
                        enabled = form.meta.canSubmit,
                        modifier = Modifier.testTag("SaveButton"),
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        // Initially cannot submit because DisplayNameField and HostField are blank
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        // Focus DisplayNameField and blur to HostField -> validation error is recorded
        onNodeWithTag(DisplayNameField).requestFocus()
        onNodeWithTag(HostField).requestFocus()
        runOnIdle {
            assertTrue(
                formState.meta.fields[DisplayNameField]
                    ?.error
                    ?.messages
                    ?.isNotEmpty() == true,
            )
        }

        // Fill DisplayNameField and HostField while leaving PathField empty -> SaveButton is enabled
        onNodeWithTag(DisplayNameField).performTextInput("MySMB")
        onNodeWithTag(HostField).performTextInput("192.168.1.10")
        onNodeWithTag("SaveButton").assertIsEnabled()

        // Clear DisplayNameField -> SaveButton is disabled immediately
        onNodeWithTag(DisplayNameField).performTextClearance()
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        // Re-enter DisplayNameField and submit with empty PathField
        onNodeWithTag(DisplayNameField).performTextInput("MySMB")
        onNodeWithTag("SaveButton").assertIsEnabled()
        onNodeWithTag("SaveButton").performClick()

        assertEquals(
            SmbEditorForm(
                displayName = "MySMB",
                host = "192.168.1.10",
                port = 445,
                path = "",
                auth = SmbEditorForm.Auth.Guest,
            ),
            submittedForm,
        )
    }

    @Test
    fun smbEditorContents_portValidation_reset_andManualHostPortErrors() = runComposeUiTest {
        val formState = FormState(
            SmbEditorForm(displayName = "MySMB", host = "192.168.1.10"),
        )

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = {})
                Column {
                    SmbEditorContents(
                        form = form,
                        uiState = BookshelfEditorScreenUiState(progress = false),
                    )
                    Button(
                        onClick = form::handleSubmit,
                        enabled = form.meta.canSubmit,
                        modifier = Modifier.testTag("SaveButton"),
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        onNodeWithTag(PortField).assertTextContains("445")
        onNodeWithTag("SaveButton").assertIsEnabled()

        // Out-of-range port disables submit
        onNodeWithTag(PortField).performTextClearance()
        onNodeWithTag(PortField).performTextInput("70000")
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        // Non-numeric input is rejected by DigitOnlyInputTransformation, leaving PortField empty
        onNodeWithTag(PortField).performTextClearance()
        onNodeWithTag(PortField).performTextInput("abc")
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        // Full-width digits are normalized to half-width ASCII digits
        onNodeWithTag(PortField).performTextInput("８０８０")
        onNodeWithTag(PortField).assertTextContains("8080")
        onNodeWithTag("SaveButton").assertIsEnabled()

        // Resetting formState updates TextFieldState values and clears errors
        runOnIdle {
            formState.reset(
                SmbEditorForm(
                    displayName = "MySMB",
                    host = "192.168.1.10",
                    port = 8080,
                    path = "share/comics",
                ),
            )
        }
        onNodeWithTag(PortField).assertTextContains("8080")
        onNodeWithTag(PathFieldName).assertTextContains("share/comics")
        onNodeWithTag("SaveButton").assertIsEnabled()

        // Setting manual errors on HostField and PortField displays them and disables submit
        runOnIdle {
            formState.setError(
                HostField to FieldError("Bad host"),
                PortField to FieldError("Bad port"),
            )
        }
        onNodeWithText("Bad host").assertIsDisplayed()
        onNodeWithText("Bad port").assertIsDisplayed()
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        // Editing HostField clears manual errors on both HostField and PortField (via dependsOn)
        onNodeWithTag(HostField).performTextClearance()
        onNodeWithTag(HostField).performTextInput("192.168.1.20")
        onNodeWithText("Bad host").assertDoesNotExist()
        onNodeWithText("Bad port").assertDoesNotExist()
        onNodeWithTag("SaveButton").assertIsEnabled()
    }

    @Test
    fun smbEditorContents_userPassAuth_requiresUsernameAndPassword() = runComposeUiTest {
        val formState = FormState(
            SmbEditorForm(displayName = "MySMB", host = "192.168.1.10"),
        )

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = {})
                Column {
                    SmbEditorContents(
                        form = form,
                        uiState = BookshelfEditorScreenUiState(progress = false),
                    )
                    Button(
                        onClick = form::handleSubmit,
                        enabled = form.meta.canSubmit,
                        modifier = Modifier.testTag("SaveButton"),
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        // Initially Guest auth is selected and SaveButton is enabled
        onNodeWithTag("SaveButton").assertIsEnabled()

        // Selecting UserPass mounts UsernameField and PasswordField, disabling SaveButton
        onNodeWithTag(SmbEditorForm.Auth.UserPass.testTag).performClick()
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        // Filling UsernameField alone is not enough while PasswordField is blank
        onNodeWithTag(UsernameField).performTextInput("testuser")
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        // Filling PasswordField enables SaveButton
        onNodeWithTag(PasswordField).performTextInput("testpass")
        onNodeWithTag("SaveButton").assertIsEnabled()

        // Clearing UsernameField disables SaveButton, and switching back to Guest re-enables it
        onNodeWithTag(UsernameField).performTextClearance()
        onNodeWithTag("SaveButton").assertIsNotEnabled()
        onNodeWithTag(SmbEditorForm.Auth.Guest.testTag).performClick()
        onNodeWithTag("SaveButton").assertIsEnabled()
    }

    @Test
    fun deviceEditorContents_requiresDisplayNameAndFolderSelection() = runComposeUiTest {
        var submittedForm: DeviceEditorForm? = null
        val formState = FormState(DeviceEditorForm())
        lateinit var folderField: FormField<String?>

        setContent {
            ComicTheme {
                val form = rememberForm(state = formState, onSubmit = { submittedForm = it })
                folderField = form.rememberField(
                    name = FolderSelectFieldName,
                    selector = { it.path },
                    updater = { copy(path = it) },
                    validator = FieldValidator { notNull { "Select folder" } },
                )
                val folderSelectFieldState = remember(folderField) {
                    object : FolderSelectFieldState {
                        override val formField: FormField<String?> = folderField
                        override val pickerResultLauncher = PickerResultLauncher {}
                    }
                }
                Column {
                    DeviceEditorContents(
                        form = form,
                        folderSelectFieldState = folderSelectFieldState,
                        uiState = BookshelfEditorScreenUiState(progress = false),
                    )
                    Button(
                        onClick = form::handleSubmit,
                        enabled = form.meta.canSubmit,
                        modifier = Modifier.testTag("SaveButton"),
                    ) {
                        Text("Save")
                    }
                }
            }
        }

        onNodeWithTag("SaveButton").assertIsNotEnabled()

        onNodeWithTag(DisplayNameField).performTextInput("LocalShelf")
        onNodeWithTag("SaveButton").assertIsNotEnabled()

        runOnIdle {
            folderField.onValueChange("/storage/comics")
        }
        onNodeWithTag("SaveButton").assertIsEnabled()
        onNodeWithTag("SaveButton").performClick()

        assertEquals(
            DeviceEditorForm(displayName = "LocalShelf", path = "/storage/comics"),
            submittedForm,
        )
    }
}
