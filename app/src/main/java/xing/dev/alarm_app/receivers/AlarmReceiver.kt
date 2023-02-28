package xing.dev.alarm_app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import xing.dev.alarm_app.util.AlarmNotificationHelper
import xing.dev.alarm_app.util.Constants
import java.util.*


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.NOTIFICATION_INTENT_ACTION_START_ALARM) {

            vibrate(context)

            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            taskRingtone = RingtoneManager.getRingtone(context, alert)
            if (taskRingtone != null) {
                taskRingtone!!.play()
            }

            sendNotification(context)

        } else if (intent.action == Constants.NOTIFICATION_INTENT_ACTION_STOP_ALARM) {
            if (taskRingtone!!.isPlaying) {
                taskRingtone!!.stop()
                vibrator!!.cancel()
            }
        }
    }


    private fun sendNotification(context: Context) {
        val alarmNotificationHelper = AlarmNotificationHelper(context)
        val notification = alarmNotificationHelper.getNotificationBuilder().build()
        alarmNotificationHelper.getManager().notify(getID(), notification)
    }

    private fun vibrate(context: Context) {
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator!!.vibrate(
                VibrationEffect.createOneShot(
                    4000,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator!!.vibrate(4000)
        }
    }

    companion object AlarmReceiver {
        var taskRingtone: Ringtone? = null
        var alert: Uri? = null
        var vibrator: Vibrator? = null

        fun getID(): Int {
            return (Date().time / 1000L % Int.MAX_VALUE).toInt()
        }
    }
}

