package org.hyperskill.phrases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import org.hyperskill.phrases.ui.CHANNEL_ID
import org.hyperskill.phrases.ui.NOTIFICATION_ID


class Notification : BroadcastReceiver() {
    val titleExtra = "titleExtra"
    val messageExtra = "messageExtra"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val builder = android.app.Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setStyle(android.app.Notification.BigTextStyle())
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}