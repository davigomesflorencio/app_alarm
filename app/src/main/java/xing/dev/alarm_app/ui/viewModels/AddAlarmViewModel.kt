package xing.dev.alarm_app.ui.viewModels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xing.dev.alarm_app.MainActivity
import xing.dev.alarm_app.R
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
    private var alarmSounds: Map<String, Int>

    private val alarmSound = MutableLiveData<Int>()
    val alarmName = MutableLiveData<String>()
    var alarmVolume = 0.5F

    var mediaPlayer: MediaPlayer? = null
    var mediaPlaying = MutableLiveData<Boolean>()

    var addVibration = MutableLiveData<Boolean>()
    private var vibrator: Vibrator


    init {
        _isAM.value = true
        mediaPlaying.value = false
        addVibration.value = false

        Calendar.getInstance().apply {
            minute = this.get(Calendar.MINUTE)
            hour = this.get(Calendar.HOUR)
            if (hour.toInt() > 12) {
                hour = hour.toInt() - 12
            }
        }

        alarmSounds =
            mapOf(
                application.getString(R.string.default_sound) to R.raw.buzzer,
                application.getString(R.string.clock_sound) to R.raw.clock,
                application.getString(R.string.rooster_sound) to R.raw.rooster
            )

        alarmName.value = application.getString(R.string.default_sound)
        alarmSound.value = alarmSounds[alarmName.value]

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

    fun setAlarmSound(sound: String) {
        alarmName.value = sound
        alarmSound.value = alarmSounds[sound]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun playSelectedAlarmSound() {
        stopAlarmSound()
        if (addVibration.value == true) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(600, 400, 600, 400),
                    intArrayOf(
                        VibrationEffect.DEFAULT_AMPLITUDE,
                        0,
                        VibrationEffect.DEFAULT_AMPLITUDE,
                        0
                    ), 0
                )
            )
        }
        mediaPlayer =
            MediaPlayer.create(application.applicationContext, alarmSound.value!!)
        mediaPlayer?.apply {
            setVolume(alarmVolume, alarmVolume)
            start()
            mediaPlaying.value = true
            setOnCompletionListener {
                it.release()
                mediaPlaying.value = false
                vibrator.cancel()
            }
        }
    }

    fun changeAlarmVolume(volume: Float) {
        alarmVolume = volume
        try {
            mediaPlayer?.setVolume(alarmVolume, alarmVolume)
        } catch (e: Exception) {
        }
    }

    fun stopAlarmSound() {
        mediaPlayer?.apply {
            try {
                vibrator.cancel()
                this.stop()
                this.release()
                mediaPlaying.value = false
            } catch (e: Exception) {
            }
        }
    }

    suspend fun saveAlarm(name: String): Boolean {
        try {
            val alarmId = UUID.randomUUID().toString()
            val alarm = Alarm(
                alarmId,
                name,
                alarmSound.value!!,
                addVibration.value!!,
                alarmVolume,
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

