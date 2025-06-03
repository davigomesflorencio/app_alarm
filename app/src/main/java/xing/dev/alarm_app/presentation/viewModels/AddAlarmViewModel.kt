package xing.dev.alarm_app.presentation.viewModels

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import xing.dev.alarm_app.core.Constants
import xing.dev.alarm_app.data.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm
import xing.dev.alarm_app.receivers.AlarmReceiver
import java.util.Calendar
import kotlin.text.insert
import kotlin.text.toInt

class AddAlarmViewModel(private val application: Application, private val alarmDao: AlarmDao) : AndroidViewModel(application) {
    var days = ArrayList<String>()
    var minute = mutableIntStateOf(0)
    var hour = mutableIntStateOf(0)

    var addVibration = MutableLiveData<Boolean>()
    var addSound = mutableStateOf(false)
    var addSnooze = mutableStateOf(false)
    private var vibrator: Vibrator

    val listDays = arrayListOf("SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM")

    var dayDomingo = mutableStateOf(false)
    var daySegunda = mutableStateOf(false)
    var dayTerca = mutableStateOf(false)
    var dayQuarta = mutableStateOf(false)
    var dayQuinta = mutableStateOf(false)
    var daySexta = mutableStateOf(false)
    var daySabado = mutableStateOf(false)

    init {
        addVibration.value = false
        addSound.value = false

        val calendar = android.icu.util.Calendar.getInstance()
        minute.intValue = calendar.get(Calendar.HOUR_OF_DAY)
        hour.intValue = calendar.get(Calendar.MINUTE)

        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibrator = vibratorManager?.defaultVibrator!!
    }

    fun addToSelectedDays(day: String) {
        days.add(day)
        val modified = days.distinct()
        days = arrayListOf()
        days.addAll(modified)
    }

    fun removeFromSelectedDays(day: String) {
        days.remove(day)
    }

    suspend fun updateAlarm(alarm: Alarm): Boolean {
        try {
            alarm.vibration = addVibration.value!!
            alarm.repeatDays = days.joinToString(separator = ",") { it }
            alarm.min = minute.intValue
            alarm.hour = hour.intValue
            alarm.disabled = true
            alarm.soundActive = addSound.value
            alarm.snoozeActive = addSnooze.value

            val alarmIdFromDb = alarm.dbId
            alarmDao.update(alarm)
            scheduleAlarmInManager(alarm, alarmIdFromDb.toInt())
            Log.d("AlarmUpdateDebug", "Alarm update successful (or at least the call completed).")
            return true
        } catch (e: Exception) {
            Log.e("AlarmUpdateDebug", "Error updating alarm in DAO", e)
            return false
        }
    }

    suspend fun saveAlarm(): Boolean {
        try {
            val alarm = Alarm(
                dbId = 0,
                vibration = addVibration.value!!,
                repeatDays = days.joinToString(separator = ",") { it },
                min = minute.intValue,
                hour = hour.intValue,
                disabled = true,
                soundActive = addSound.value,
                snoozeActive = addSnooze.value
            )
            val alarmIdFromDb = alarmDao.insert(alarm)
            val alarmToSchedule = alarm.copy(dbId = alarmIdFromDb)
            scheduleAlarmInManager(alarmToSchedule, alarmIdFromDb.toInt())
            return true
        } catch (_: Exception) {
            return false
        }
    }

    // Extracted scheduling logic for clarity and reuse
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarmInManager(alarm: Alarm, uniqueRequestCodeBase: Int) {
        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (alarm.repeatDays.isEmpty()) {
            // Non-repeating alarm
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.min)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1) // If time has passed today, set for tomorrow
            }

            val alarmIntent = Intent(application, AlarmReceiver::class.java).apply {
                action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                // Pass the database ID, so the receiver knows which alarm to handle
                putExtra(Constants.ALARM_ID_EXTRA, alarm.dbId)
                putExtra(Constants.EXTRA_ALARM_TIME_HOUR, alarm.hour)
                putExtra(Constants.EXTRA_ALARM_TIME_MINUTE, alarm.min)
                putExtra(Constants.EXTRA_ALARM_SOUND_ENABLED, alarm.soundActive)
                putExtra(Constants.EXTRA_ALARM_VIBRATION_ENABLED, alarm.vibration)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                application,
                uniqueRequestCodeBase, // Use the alarm's database ID as the request code for uniqueness
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        } else {
            val selectedDaysList = alarm.repeatDays.split(",")
            selectedDaysList.forEachIndexed { index, dayString ->
                val dayOfWeek = selectDayOfWeek(dayString) // Ensure this returns Calendar.DAY_OF_WEEK constants
                if (dayOfWeek == -1) return@forEachIndexed // Skip if day string is invalid

                val calendar: Calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, alarm.hour)
                    set(Calendar.MINUTE, alarm.min)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    set(Calendar.DAY_OF_WEEK, dayOfWeek)
                }

                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1) // If time passed this week, schedule for next week
                }

                val requestCodeForRepeating = uniqueRequestCodeBase + dayOfWeek // Or use index, ensure it's unique

                val alarmIntent = Intent(application, AlarmReceiver::class.java).apply {
                    action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                    putExtra(Constants.ALARM_ID_EXTRA, alarm.dbId)
                    putExtra(Constants.ALARM_ID_EXTRA, alarm.dbId)
                    putExtra(Constants.EXTRA_ALARM_TIME_HOUR, alarm.hour)
                    putExtra(Constants.EXTRA_ALARM_TIME_MINUTE, alarm.min)
                    putExtra(Constants.EXTRA_ALARM_SOUND_ENABLED, alarm.soundActive)
                    putExtra(Constants.EXTRA_ALARM_VIBRATION_ENABLED, alarm.vibration)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    application,
                    requestCodeForRepeating,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
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

