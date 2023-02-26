package xing.dev.alarm_app.ui.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import xing.dev.alarm_app.domain.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm

class AlarmsViewModel(private val application: Application, private val alarmDao: AlarmDao) :
    ViewModel() {
    var alarms: LiveData<List<Alarm>> = alarmDao.getAlarms()
}