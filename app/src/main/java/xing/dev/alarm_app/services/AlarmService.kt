package xing.dev.alarm_app.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import xing.dev.alarm_app.receivers.AlarmReceiver
import xing.dev.alarm_app.util.Constants
import java.util.*


class AlarmService : JobIntentService() {
    private val notificationId = System.currentTimeMillis().toInt()

    override fun onHandleWork(intent: Intent) {
        val action = intent.action

        if (action == Constants.ACTION_STOP_ALARM) {
            if (AlarmReceiver.taskRingtone!!.isPlaying) {
                AlarmReceiver.taskRingtone!!.stop()
                AlarmReceiver.vibrator!!.cancel()
            }
        } else if (action == Constants.ACTION_SNOOZE_ALARM) {
            snoozeAlarm()
        }
    }

    private fun snoozeAlarm() {
        if (AlarmReceiver.taskRingtone!!.isPlaying) {
            AlarmReceiver.taskRingtone!!.stop()
            AlarmReceiver.vibrator!!.cancel()
        }

        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, notificationId, intent, 0)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + 5 * 6000, pendingIntent
        )
    }

}