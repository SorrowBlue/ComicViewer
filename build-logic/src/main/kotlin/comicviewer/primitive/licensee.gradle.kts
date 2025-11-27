package comicviewer.primitive

import app.cash.licensee.UnusedAction

plugins {
    app.cash.licensee
}

licensee {
    allow("Apache-2.0")
    allow("EPL-1.0")
    allow("BSD-2-Clause")
    allow("MIT")
    allow("Unlicense")
    allowUrl("https://developer.android.com/studio/terms.html")
    allowUrl("https://developer.android.com/guide/playcore/license")
    allowUrl("https://opensource.org/license/LGPL-2.1")
    allowUrl("http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt")
    allowUrl("https://www.gnu.org/licenses/agpl-3.0.html")

    allowUrl("https://www.gnu.org/licenses/lgpl.txt")
    allowUrl("https://www.bouncycastle.org/licence.html")
    allowUrl("http://www.7-zip.org/license.txt")
    allowUrl("https://github.com/hypfvieh/dbus-java/blob/master/LICENSE")

    // MIT
    allowUrl("https://github.com/vinceglb/FileKit/blob/main/LICENSE")
    allowUrl("https://opensource.org/license/mit")
    allowUrl("https://github.com/zacharee/KMPFile/blob/main/LICENSE")

    allowDependency(
        "com.github.shayartzi.sevenzipjbinding",
        "sevenzipjbinding-all-platforms",
        "16.02-2.01",
    ) {
        because("LGPL, but typo in license URL fixed in newer versions")
    }

    unusedAction(UnusedAction.IGNORE)
}
