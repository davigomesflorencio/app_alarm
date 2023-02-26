package xing.dev.alarm_app.ui.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xing.dev.alarm_app.domain.dao.AlarmDao

class AlarmsViewModelFactory(private val application: Application, private val alarmDao: AlarmDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmsViewModel::class.java)) {
            return AlarmsViewModel(application, alarmDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}