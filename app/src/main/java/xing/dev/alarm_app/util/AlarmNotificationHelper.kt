package xing.dev.alarm_app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import xing.dev.alarm_app.MainActivity
import xing.dev.alarm_app.R
import xing.dev.alarm_app.services.AlarmService

class AlarmNotificationHelper(base: Context) : ContextWrapper(base) {
    private val notificationId = System.currentTimeMillis().toInt()

    private var manager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "alarm_name"
            val descriptionText = "Canal de Mensagens"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.enableVibration(true)
            getManager().createNotificationChannel(channel)
        }
    }

    fun getManager(): NotificationManager {
        if (manager == null) manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    fun getNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            action = Constants.ACTION_STOP_ALARM
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
            .setContentText("Alarme")
            .setSmallIcon(R.drawable.alarm)
            .setColor(Color.YELLOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .addAction(R.drawable.ic_baseline_play_arrow_24, "Parar", stopAlarmTone(this))
            .addAction(R.drawable.ic_baseline_snooze_24, "Soneca", snoozeAlarm(this))

    }


    private fun stopAlarmTone(context: Context): PendingIntent {
        val stopAlarmIntent = Intent(context, AlarmService::class.java).apply {
            action = Constants.ACTION_STOP_ALARM
        }
        return PendingIntent.getService(
            context, Constants.NOTIFICATION_ID, stopAlarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    }

    private fun snoozeAlarm(context: Context): PendingIntent {
        val snoozeIntent = Intent(
            context,
            AlarmService::class.java
        ).apply {
            action = Constants.ACTION_SNOOZE_ALARM
        }
        return PendingIntent.getService(
            context, notificationId, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}