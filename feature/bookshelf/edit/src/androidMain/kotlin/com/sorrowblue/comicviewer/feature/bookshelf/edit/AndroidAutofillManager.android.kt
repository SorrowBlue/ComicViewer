package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import android.view.autofill.AutofillManager
import androidx.core.content.getSystemService
import org.koin.core.annotation.Factory

@Factory
internal actual class AndroidAutofillManager(private val context: Context) {
    actual fun commit() {
        context.getSystemService<AutofillManager>()!!.commit()
    }
}
