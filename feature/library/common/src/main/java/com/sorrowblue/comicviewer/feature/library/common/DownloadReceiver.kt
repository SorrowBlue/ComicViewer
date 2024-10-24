package com.sorrowblue.comicviewer.feature.library.common

import android.Manifest
import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.documentfile.provider.DocumentFile
import com.sorrowblue.comicviewer.framework.notification.ChannelID

internal class DownloadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val file = context.getSystemService<DownloadManager>()!!.getUriForDownloadedFile(id)
            val fileName = DocumentFile.fromSingleUri(context, file)?.name
            val notification = Notification.Builder(context, ChannelID.DOWNLOAD.id)
                .setSmallIcon(com.sorrowblue.comicviewer.framework.designsystem.R.drawable.ic_notification)
                .setContentTitle("1個のファイルをダウンロードしました")
                .setContentText(fileName)
                .setAutoCancel(true)
                .addAction(
                    Notification.Action.Builder(
                        Icon.createWithResource(
                            context,
                            com.sorrowblue.comicviewer.framework.designsystem.R.drawable.ic_open_in_new_24
                        ),
                        "開く",
                        PendingIntent.getActivity(
                            context,
                            0,
                            Intent(DownloadManager.ACTION_VIEW_DOWNLOADS),
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    ).build()
                )
                .build()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                context.getSystemService<NotificationManager>()?.notify(id.toInt(), notification)
            }
        } else if (intent.action == DownloadManager.ACTION_NOTIFICATION_CLICKED) {
            // ダウンロードマネージャの通知領域をクリックした場合はメッセージ表示のみ
            Toast.makeText(context, intent.action, Toast.LENGTH_LONG).show()
        }
    }
}
