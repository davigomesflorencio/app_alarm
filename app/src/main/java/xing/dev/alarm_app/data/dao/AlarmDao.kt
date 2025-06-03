package xing.dev.alarm_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import xing.dev.alarm_app.domain.model.Alarm

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: Alarm): Long

    @Delete
    suspend fun delete(alarm: Alarm)

    @Update
    suspend fun update(alarm: Alarm)

    @Query("SELECT * FROM alarms ORDER BY hour, minute ASC")
    fun getAlarms(): Flow<List<Alarm>>

    @Query("SELECT*FROM alarms WHERE dbId=:id LIMIT 1")
    suspend fun getAlarm(id: Long): Alarm?
}