package xing.dev.alarm_app.ui.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xing.dev.alarm_app.R
import xing.dev.alarm_app.databinding.AlarmListItemBinding
import xing.dev.alarm_app.domain.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm
import xing.dev.alarm_app.receivers.AlarmReceiver
import xing.dev.alarm_app.util.Constants
import xing.dev.alarm_app.util.selectDayOfWeek
import java.util.*

class AlarmAdapter(private val alarmDao: AlarmDao) :
    ListAdapter<Alarm, AlarmAdapter.ViewHolder>(AlarmDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AlarmListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, alarmDao)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm)
    }

    class ViewHolder(private val binding: AlarmListItemBinding, private val alarmDao: AlarmDao) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alarm: Alarm) {
            binding.disableSwitch.isChecked = !alarm.disabled
            binding.txtDayWeeks.text = alarm.formattedRepeatDays()
            binding.txtHour.text = alarm.formattedTime()

            binding.disableSwitch.setOnCheckedChangeListener { switch, b ->
                alarm.let {
                    it.disabled = !b
                    (switch as SwitchMaterial).apply {
                        if (it.disabled) {
                            trackDrawable.setTint(context.getColor(R.color.lightGrey))
                            cancelAlarm(it.dbId)
                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                val saved = updateAlarm(it.dbId)
                                if (!saved) {
                                    Toast.makeText(
                                        itemView.context,
                                        "Não foi possível agendar o Alarme!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    trackDrawable.setTint(context.getColor(R.color.primaryColorLight))
                                }
                            }
                        }
                    }
                }
                runBlocking {
                    alarmDao.update(alarm)
                }

            }

            binding.deleteIcon.setOnClickListener {
                cancelAlarm(alarm.dbId)
                runBlocking { alarmDao.delete(alarm) }
            }


        }

        private fun cancelAlarm(id: Long) {
            val alarmManager =
                itemView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(Constants.NOTIFICATION_INTENT_ACTION_START_ALARM).setClass(
                itemView.context,
                AlarmReceiver::class.java
            )
            val pendingIntent =
                PendingIntent.getBroadcast(
                    itemView.context,
                    id.toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            alarmManager.cancel(pendingIntent)
        }

        suspend fun updateAlarm(id: Long): Boolean {
            try {
                cancelAlarm(id)
                val alarmManager =
                    itemView.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val foundAlarm = alarmDao.getAlarm(id)

                if (foundAlarm != null) {
                    if (foundAlarm.repeatDays.isEmpty()) {
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, foundAlarm.hour)
                            set(Calendar.MINUTE, foundAlarm.min)
                        }
                        if (calendar.before(Calendar.getInstance())) {
                            calendar.add(Calendar.DATE, 1)
                        }

                        val alarmIntent = Intent(itemView.context, AlarmReceiver::class.java).let {
                            it.action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                            it.putExtra("id", foundAlarm.dbId)
                            PendingIntent.getBroadcast(
                                itemView.context,
                                foundAlarm.dbId.toInt(),
                                it,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        }

                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            alarmIntent
                        )
                    } else {

                        foundAlarm.repeatDays.forEachIndexed { _, element ->
                            val calendar: Calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR, foundAlarm.hour)
                                set(Calendar.MINUTE, foundAlarm.min)
                                set(Calendar.MILLISECOND, 0)
                                set(Calendar.DAY_OF_WEEK, selectDayOfWeek(element))
                            }

                            if (calendar.before(Calendar.getInstance())) {
                                calendar.add(Calendar.DATE, 7)
                            }

                            val alarmIntent =
                                Intent(itemView.context, AlarmReceiver::class.java).let {
                                    it.action = Constants.NOTIFICATION_INTENT_ACTION_START_ALARM
                                    it.putExtra("id", foundAlarm.dbId)
                                    PendingIntent.getBroadcast(
                                        itemView.context,
                                        foundAlarm.dbId.toInt(),
                                        it,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                    )
                                }

                            alarmManager.set(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                alarmIntent
                            )
                        }
                    }
                }

                return true
            } catch (e: Exception) {
                return false
            }
        }
    }

}

class AlarmDiffCallback : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.dbId == newItem.dbId
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }
}
