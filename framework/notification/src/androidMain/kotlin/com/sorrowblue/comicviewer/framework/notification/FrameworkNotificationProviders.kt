package com.sorrowblue.comicviewer.framework.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface FrameworkNotificationProviders {

    @Provides
    fun provideNotificationManagerCompat(context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)
}
