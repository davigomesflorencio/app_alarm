package xing.dev.alarm_app.ui.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import xing.dev.alarm_app.R
import xing.dev.alarm_app.databinding.FragmentAlarmsBinding
import xing.dev.alarm_app.domain.database.AlarmDatabase
import xing.dev.alarm_app.ui.adapter.AlarmAdapter
import xing.dev.alarm_app.ui.viewModels.AlarmsViewModel
import xing.dev.alarm_app.ui.viewModels.AlarmsViewModelFactory


class AlarmsFragment : Fragment() {

    private lateinit var viewModel: AlarmsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAlarmsBinding.inflate(
            layoutInflater, container, false
        )
        (activity as AppCompatActivity).supportActionBar?.hide()

        val application: Application = requireNotNull(this.activity).application
        val alarmDao = AlarmDatabase.getInstance(application).alarmDao
        val viewModelFactory = AlarmsViewModelFactory(application, alarmDao)
        viewModel = ViewModelProvider(this, viewModelFactory)[AlarmsViewModel::class.java]

        val adapter = AlarmAdapter(alarmDao)

        binding.AlarmsRecyclerView.adapter = adapter

        viewModel.alarms.observe(viewLifecycleOwner) {
            it?.apply {
                adapter.submitList(this)
            }
            if (it.isEmpty()) {
                binding.imgViewAlarm.visibility = View.VISIBLE
                binding.AlarmsRecyclerView.visibility = View.GONE
            } else {
                binding.imgViewAlarm.visibility = View.GONE
                binding.AlarmsRecyclerView.visibility = View.VISIBLE
            }
        }
        binding.addAlarmButton.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_alarmsFragment_to_addAlarmFragment)
        }

        return binding.root
    }

}