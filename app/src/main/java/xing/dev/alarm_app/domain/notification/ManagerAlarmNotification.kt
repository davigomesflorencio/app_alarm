package xing.dev.alarm_app.domain.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import xing.dev.alarm_app.MainActivity
import xing.dev.alarm_app.R
import xing.dev.alarm_app.core.Constants
import xing.dev.alarm_app.receivers.WorkerTriggerReceiver

class ManagerAlarmNotification(base: Context) : ContextWrapper(base) {
    private val notificationId = System.currentTimeMillis().toInt()

    private var manager: NotificationManager? = null

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "alarm_name"
        val descriptionText = "Canal de Mensagens"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        channel.enableVibration(true)
        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager {
        if (manager == null) manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    fun getNotificationBuilder(messageBody: String): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.action = Constants.ACTION_STOP_ALARM
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_alarm_fill)
            .setColor(Color.WHITE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_baseline_stop_24,
                "Parar",
                stopAlarmTone(this)
            )
            .addAction(R.drawable.ic_baseline_snooze_24, "Soneca", snoozeAlarm(this))

    }

    private fun stopAlarmTone(context: Context): PendingIntent {
        val stopAlarmIntent = Intent(context, WorkerTriggerReceiver::class.java)
        stopAlarmIntent.action = Constants.ACTION_STOP_ALARM
        return PendingIntent.getBroadcast(
            context, Constants.NOTIFICATION_ID, stopAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    }

    private fun snoozeAlarm(context: Context): PendingIntent {
        val snoozeIntent = Intent(
            context,
            WorkerTriggerReceiver::class.java
        )
        snoozeIntent.action = Constants.ACTION_SNOOZE_ALARM
        return PendingIntent.getBroadcast(
            context, notificationId, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}