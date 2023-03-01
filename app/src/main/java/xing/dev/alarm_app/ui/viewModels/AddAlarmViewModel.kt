package xing.dev.alarm_app.ui.viewModels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xing.dev.alarm_app.domain.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm
import xing.dev.alarm_app.receivers.AlarmReceiver
import xing.dev.alarm_app.util.Constants
import xing.dev.alarm_app.util.selectDayOfWeek
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
class AddAlarmViewModel(private val application: Application, private val alarmDao: AlarmDao) :
    ViewModel() {
    var days = ArrayList<String>()
    var minute: Number
    var hour: Number

    var addVibration = MutableLiveData<Boolean>()
    private var vibrator: Vibrator


    init {
        addVibration.value = false

        Calendar.getInstance().apply {
            minute = this.get(Calendar.MINUTE)
            hour = this.get(Calendar.HOUR_OF_DAY)
        }

        vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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

    suspend fun saveAlarm(): Boolean {
        try {
            val alarm = Alarm(
                dbId = 0,
                vibration = addVibration.value!!,
                repeatDays = days,
                min = minute.toInt(),
                hour = hour.toInt(),
                disabled = false,
            )
            val alarmId = alarmDao.insert(alarm)

            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (alarm.repeatDays.isEmpty()) {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, alarm.hour)
                    set(Calendar.MINUTE, alarm.min)
                }
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1)
                }

                val alarmIntent = Intent(application, AlarmReceiver::class.java).let {
                    it.action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                    it.putExtra("id", alarm.dbId)
                    PendingIntent.getBroadcast(
                        application,
                        alarmId.toInt(),
                        it,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
            }else{
                days.forEachIndexed { _, element ->
                    val calendar: Calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR, hour.toInt())
                        set(Calendar.MINUTE, minute.toInt())
                        set(Calendar.MILLISECOND, 0)
                        set(Calendar.DAY_OF_WEEK, selectDayOfWeek(element))
                    }

                    if (calendar.before(Calendar.getInstance())) {
                        calendar.add(Calendar.DATE, 7)
                    }

                    val alarmIntent = Intent(application, AlarmReceiver::class.java).let {
                        it.action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                        it.putExtra("id", alarmId)
                        PendingIntent.getBroadcast(
                            application,
                            alarmId.toInt(),
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
                }
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }

}

