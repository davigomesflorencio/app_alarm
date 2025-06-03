package xing.dev.alarm_app.domain.vibration

import android.Manifest
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import java.util.Date

class ManagerVibrationAndSound(val context: Context) {

    var taskRingtone: Ringtone? = null
    var alert: Uri? = null
    var vibrator: Vibrator? = null

    fun init() {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator = vibratorManager.defaultVibrator
        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        taskRingtone = RingtoneManager.getRingtone(context, alert)
    }

    fun playRingTone() {
        taskRingtone?.play()
    }

    fun stopRingTone() {
        taskRingtone?.let {
            if (taskRingtone!!.isPlaying)
                taskRingtone?.stop()
        }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun vibrateDevice() {
        if (vibrator?.hasVibrator() == true) {
            val vibrationEffect = VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 300), 0)
            vibrator?.vibrate(vibrationEffect)
        }
    }

    fun cancelVibrate() {
        vibrator?.cancel()
    }

    fun getID(): Int {
        return (Date().time / 1000L % Int.MAX_VALUE).toInt()
    }
}