package xing.dev.alarm_app.ui.viewModels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xing.dev.alarm_app.MainActivity
import xing.dev.alarm_app.domain.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm
import xing.dev.alarm_app.receivers.AlarmReceiver
import xing.dev.alarm_app.util.Constants
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
class AddAlarmViewModel(private val application: Application, private val alarmDao: AlarmDao) :
    ViewModel() {
    private val _isAM = MutableLiveData<Boolean>()
    val isAM: LiveData<Boolean>
        get() = _isAM
    var days = ArrayList<String>()
    var minute: Number
    var hour: Number

    var addVibration = MutableLiveData<Boolean>()
    private var vibrator: Vibrator


    init {
        _isAM.value = true
        addVibration.value = false

        Calendar.getInstance().apply {
            minute = this.get(Calendar.MINUTE)
            hour = this.get(Calendar.HOUR)
            if (hour.toInt() > 12) {
                hour = hour.toInt() - 12
            }
        }


        vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun setAM(value: Boolean) {
        _isAM.value = value
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
            val alarmId = UUID.randomUUID().toString()
            val alarm = Alarm(
                alarmId,
                addVibration.value!!,
                days,
                minute.toInt(),
                hour.toInt(),
                isAM.value!!,
                false
            )
            alarmDao.insert(alarm)

            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar: Calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR, if (hour.toInt() == 12) 0 else hour.toInt())
                set(Calendar.MINUTE, minute.toInt())
                set(Calendar.MILLISECOND, 0)
                set(Calendar.AM_PM, if (isAM.value!!) Calendar.AM else Calendar.PM)
            }

            val alarmIntent = Intent(application, AlarmReceiver::class.java).let {
                it.action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                it.putExtra("id", alarmId)
                PendingIntent.getBroadcast(application, 0, it, PendingIntent.FLAG_CANCEL_CURRENT)
            }

            val intent2 = Intent(application, MainActivity::class.java).let {
                PendingIntent.getActivity(application, 1, it, 0)
            }

            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent2),
                alarmIntent
            )
            return true
        } catch (e: Exception) {
            return false
        }
    }
}

