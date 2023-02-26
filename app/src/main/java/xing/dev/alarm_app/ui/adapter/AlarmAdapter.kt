package xing.dev.alarm_app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.runBlocking
import xing.dev.alarm_app.R
import xing.dev.alarm_app.databinding.AlarmListItemBinding
import xing.dev.alarm_app.domain.dao.AlarmDao
import xing.dev.alarm_app.domain.model.Alarm

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
            binding.txtAlarmName.text = alarm.name
            binding.txtDayWeeks.text = alarm.formattedRepeatDays()
            binding.txtHour.text = alarm.formattedTime()
            binding.txtIsAM.text = if (alarm.isAM) "AM" else "PM"

            binding.disableSwitch.setOnCheckedChangeListener { switch, b ->
                alarm?.let {
                    it.disabled = !b
                    (switch as SwitchMaterial).apply {
                        if (it.disabled) {
                            trackDrawable.setTint(context.getColor(R.color.lightGrey))
                        } else {
                            trackDrawable.setTint(context.getColor(R.color.primaryColorLight))
                        }
                    }
                }
                runBlocking { alarmDao.update(alarm) }
            }

            binding.deleteIcon.setOnClickListener {
                runBlocking { alarmDao.delete(alarm) }
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
