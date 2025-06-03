package xing.dev.alarm_app.presentation.viewModels

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xing.dev.alarm_app.core.Constants
import xing.dev.alarm_app.data.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm
import xing.dev.alarm_app.receivers.AlarmReceiver
import java.util.Calendar

class AlarmsViewModel(
    private val application: Application,
    private val alarmDao: AlarmDao
) : AndroidViewModel(application) {

    var alarms = mutableStateOf<List<Alarm>>(emptyList())

    val listDays = arrayListOf("SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM")

    init {
        viewModelScope.launch {
            alarmDao.getAlarms().collect {
                alarms.value = it
            }
        }
    }

    fun disable(alarm: Alarm) {
        viewModelScope.launch {
            val new = alarm.copy()
            new.disabled = false
            alarmDao.update(new)
        }
    }

    fun activateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val new = alarm.copy()
            new.disabled = true
            alarmDao.update(new)
        }
    }

    /**
     * Deletes an alarm from the database, cancels its scheduled PendingIntents,
     * and dismisses its notification.
     *
     * @param alarm The alarm to delete.
     */
    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmDao.delete(alarm)
            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarm.repeatDays.isEmpty()) {
                val intent = Intent(application, AlarmReceiver::class.java).apply {
                    action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    application,
                    alarm.dbId.toInt(), // Crucial: Use the same request code used for scheduling
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel() // Also explicitly cancel the PendingIntent object
            } else {
                val selectedDaysList = alarm.repeatDays.split(",")
                selectedDaysList.forEachIndexed { index, dayString ->
                    val dayOfWeek = selectDayOfWeek(dayString)
                    if (dayOfWeek == -1) return@forEachIndexed

                    val requestCodeForRepeating = alarm.dbId.toInt() + dayOfWeek // Must match scheduling

                    val intent = Intent(application, AlarmReceiver::class.java).apply {
                        action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        application,
                        requestCodeForRepeating,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }

            val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(alarm.dbId.toInt())
        }
    }

    fun selectDayOfWeek(element: String): Int {
        when (element) {
            "SEG" -> {
                return Calendar.MONDAY
            }

            "TER" -> {
                return Calendar.TUESDAY
            }

            "QUA" -> {
                return Calendar.WEDNESDAY
            }

            "QUI" -> {
                return Calendar.THURSDAY
            }

            "SEX" -> {
                return Calendar.FRIDAY
            }

            "SAB" -> {
                return Calendar.SATURDAY
            }

            "DOM" -> {
                return Calendar.SUNDAY
            }
        }
        return -1
    }
}