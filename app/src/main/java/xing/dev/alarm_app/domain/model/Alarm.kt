package xing.dev.alarm_app.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "alarms")
@TypeConverters(StringArrayConverter::class)
data class Alarm(
    @PrimaryKey(autoGenerate = false) var dbId: String = "",
    var vibration: Boolean,
    var repeatDays: List<String>,
    var min: Int,
    var hour: Int,
    var isAM: Boolean,
    var disabled: Boolean
) {
    fun formattedTime(): String {
        return hour.toString().padStart(2, '0') + ":" + min.toString().padStart(2, '0')
    }

    fun formattedRepeatDays(): String {
        if (repeatDays.isEmpty()) {
            return "No repeats"
        }
        return repeatDays.joinToString(", ")
    }
}

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object StringArrayConverter {
    @ExperimentalStdlibApi
    @TypeConverter
    fun fromString(daysString: String): List<String> {
        val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter()
        return requireNotNull(jsonAdapter.fromJson(daysString))
    }

    @ExperimentalStdlibApi
    @TypeConverter
    fun toString(list: List<String>): String {
        val jsonAdapter: JsonAdapter<List<String>> = moshi.adapter()
        return jsonAdapter.toJson(list)
    }
}