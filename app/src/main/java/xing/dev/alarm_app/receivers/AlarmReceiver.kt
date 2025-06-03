package xing.dev.alarm_app.receivers

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xing.dev.alarm_app.core.Constants
import xing.dev.alarm_app.domain.notification.ManagerAlarmNotification
import xing.dev.alarm_app.domain.vibration.ManagerVibrationAndSound
import java.util.Locale


class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val managerVibrationAndSound: ManagerVibrationAndSound by inject()

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.NOTIFICATION_INTENT_ACTION_START_ALARM) {
            val alarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1) // Get ID if needed
            val hour = intent.getIntExtra(Constants.EXTRA_ALARM_TIME_HOUR, -1)
            val minute = intent.getIntExtra(Constants.EXTRA_ALARM_TIME_MINUTE, -1)
            val isSoundEnabled = intent.getBooleanExtra(Constants.EXTRA_ALARM_SOUND_ENABLED, true) // Default to true if not found
            val isVibrationEnabled = intent.getBooleanExtra(Constants.EXTRA_ALARM_VIBRATION_ENABLED, true) // Default to true if not found
            val alarmLabel = intent.getStringExtra(Constants.EXTRA_ALARM_LABEL) ?: "Alarm"

            val formattedTime = if (hour != -1 && minute != -1) {
                String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            } else {
                "Alarme"
            }

            managerVibrationAndSound.init()

            if (isSoundEnabled)
                managerVibrationAndSound.playRingTone()
            if (isVibrationEnabled)
                managerVibrationAndSound.vibrateDevice()

            sendNotification(context, "Alarme : $formattedTime")

        } else if (intent.action == Constants.NOTIFICATION_INTENT_ACTION_STOP_ALARM) {
            val alarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, -1) // Get ID if needed
            val hour = intent.getIntExtra(Constants.EXTRA_ALARM_TIME_HOUR, -1)
            val minute = intent.getIntExtra(Constants.EXTRA_ALARM_TIME_MINUTE, -1)
            val isSoundEnabled = intent.getBooleanExtra(Constants.EXTRA_ALARM_SOUND_ENABLED, true) // Default to true if not found
            val isVibrationEnabled = intent.getBooleanExtra(Constants.EXTRA_ALARM_VIBRATION_ENABLED, true) // Default to true if not found
            val alarmLabel = intent.getStringExtra(Constants.EXTRA_ALARM_LABEL) ?: "Alarm"

            if (isSoundEnabled)
                managerVibrationAndSound.stopRingTone()
            if (isVibrationEnabled)
                managerVibrationAndSound.cancelVibrate()

        }
    }

    private fun sendNotification(context: Context, body: String) {
        val managerAlarmNotification = ManagerAlarmNotification(context)
        val notification = managerAlarmNotification.getNotificationBuilder(body).build()
        managerAlarmNotification.getManager().notify(managerVibrationAndSound.getID(), notification)
    }
}


