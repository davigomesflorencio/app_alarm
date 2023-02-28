package xing.dev.alarm_app.ui.viewModels

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xing.dev.alarm_app.domain.dao.AlarmDao

class AddAlarmViewModelFactory(
    private val application: Application,
    private val alarmDao: AlarmDao
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAlarmViewModel::class.java)) {
            return AddAlarmViewModel(application, alarmDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}