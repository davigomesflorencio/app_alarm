package xing.dev.alarm_app.services

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import xing.dev.alarm_app.receivers.AlarmReceiver
import xing.dev.alarm_app.util.Constants
import java.util.*


class AlarmService : IntentService(AlarmService::class.java.simpleName) {
    private val notificationId = System.currentTimeMillis().toInt()
    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action

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