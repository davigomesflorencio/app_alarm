package xing.dev.alarm_app.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import xing.dev.alarm_app.domain.model.Alarm

@Dao
interface AlarmDao {
    @Insert
    suspend fun insert(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Update
    suspend fun update(alarm: Alarm)

    @Query("SELECT * FROM alarms")
    fun getAlarms(): LiveData<List<Alarm>>

    @Query("SELECT*FROM alarms WHERE dbId=:id LIMIT 1")
    suspend fun getAlarm(id: String): Alarm?
}