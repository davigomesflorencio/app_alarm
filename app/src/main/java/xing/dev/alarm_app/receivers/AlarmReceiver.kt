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

//            Feature a implementar : setar hora do alarme na notificação
//            val db by lazy {
//                AlarmDatabase.getInstance(context).alarmDao
//            }

            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            taskRingtone = RingtoneManager.getRingtone(context, alert)
            if (taskRingtone != null) {
                taskRingtone!!.play()
                vibrate()
            }

            sendNotification(context, "Alarme")

        } else if (intent.action == Constants.NOTIFICATION_INTENT_ACTION_STOP_ALARM) {
            if (taskRingtone!!.isPlaying) {
                taskRingtone!!.stop()
                vibrator!!.cancel()
            }
        }
    }


    private fun sendNotification(context: Context, messageBody: String) {
        val alarmNotificationHelper = AlarmNotificationHelper(context)
        val notification = alarmNotificationHelper.getNotificationBuilder(messageBody).build()
        alarmNotificationHelper.getManager().notify(getID(), notification)
    }

    private fun vibrate() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= 26) {
                it.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(200)
            }
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

