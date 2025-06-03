package xing.dev.alarm_app.worker

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xing.dev.alarm_app.core.Constants
import xing.dev.alarm_app.domain.vibration.ManagerVibrationAndSound
import xing.dev.alarm_app.receivers.AlarmReceiver
import java.util.Calendar

class AlarmWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val managerVibrationAndSound: ManagerVibrationAndSound by inject()
    private val notificationId = System.currentTimeMillis().toInt()

    @RequiresPermission(Manifest.permission.VIBRATE)
    override suspend fun doWork(): Result {
        if (Thread.interrupted())
            throw InterruptedException()

        val action = inputData.getString("action")

        if (action == Constants.ACTION_STOP_ALARM) {
            managerVibrationAndSound.stopRingTone()
            managerVibrationAndSound.cancelVibrate()
        } else if (action == Constants.ACTION_SNOOZE_ALARM) {
            snoozeAlarm()
        }
        return Result.success()
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun snoozeAlarm() {
        managerVibrationAndSound.stopRingTone()
        managerVibrationAndSound.cancelVibrate()

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, notificationId, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + 5 * 60000,
            pendingIntent
        )
    }
}