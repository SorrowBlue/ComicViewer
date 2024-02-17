package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

internal class SmbEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun smbEditScreen() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val state = SmbEditScreenState(
            uiState = SmbEditScreenUiState(),
            args = BookshelfEditArgs(),
            context = context,
            snackbarHostState = SnackbarHostState(),
            scope = TestScope(),
            registerBookshelfUseCase = object : RegisterBookshelfUseCase() {
                override fun run(request: Request): Flow<Resource<Bookshelf, Error>> {
                    TODO("Not yet implemented")
                }
            },
            softwareKeyboardController = object : SoftwareKeyboardController {
                override fun hide() {
                    TODO("Not yet implemented")
                }

                override fun show() {
                    TODO("Not yet implemented")
                }

            }
        )
        composeTestRule.setContent {
            SmbEditScreen(state = state, onBackClick = { /*TODO*/ }) {

            }
        }

        composeTestRule.onNodeWithContentDescription("DisplayName").performTextInput("Test")
        assertEquals(state.uiState.displayName.value, "Test")
    }
}
