package xing.dev.alarm_app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import xing.dev.alarm_app.data.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm

@Database(
    entities = [Alarm::class],
    version = 8,
    exportSchema = true
)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun getAlarmDatabaseDao(): AlarmDao
}