package xing.dev.alarm_app.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import xing.dev.alarm_app.domain.model.Alarm

@Serializable
data object Alarms : NavKey

@Serializable
data object AddAlarm : NavKey

@Serializable
data object DetailsAlarm : NavKey

@Serializable
data object AlarmDaysWeekScreen : NavKey

@Serializable
data class EditAlarm(val alarm: Alarm) : NavKey

@Serializable
data class EditDetailsAlarm(val alarm: Alarm) : NavKey