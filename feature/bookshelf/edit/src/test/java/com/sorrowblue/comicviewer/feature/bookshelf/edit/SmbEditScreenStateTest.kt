package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SmbEditScreenStateTest {

    private lateinit var screenState: SmbEditScreenState

    @Before
    fun setup() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        screenState = SmbEditScreenState(
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
    }

    @Test
    fun onHostChange_empty() {
        screenState.onHostChange("")
        assertThat(screenState.uiState.host.isError).isEqualTo(true)
    }

    @Test
    fun onHostChange_pc_name() {
        screenState.onHostChange("PC_NAME")
        assertThat(screenState.uiState.host.isError).isEqualTo(false)
    }

    @Test
    fun onHostChange_ip() {
        screenState.onHostChange("127.0.0.1")
        assertThat(screenState.uiState.host.isError).isEqualTo(false)
    }

    @Test
    fun onDisplayNameChange_not_empty() {
        screenState.onDisplayNameChange("displayName")
        assertThat(screenState.uiState.displayName.isError).isEqualTo(false)
    }

    @Test
    fun onDisplayNameChange_empty() {
        screenState.onDisplayNameChange("")
        assertThat(screenState.uiState.displayName.isError).isEqualTo(true)
    }
}
