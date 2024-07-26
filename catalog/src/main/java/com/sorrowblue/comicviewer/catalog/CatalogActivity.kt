package com.sorrowblue.comicviewer.catalog

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.android.showkase.ui.ShowkaseBrowserActivity

internal class CatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, ShowkaseBrowserActivity::class.java)
        intent.putExtra(
            "SHOWKASE_ROOT_MODULE",
            "com.sorrowblue.comicviewer.catalog.CatalogRootModule"
        )
        startActivity(intent)
        finish()
    }
}
