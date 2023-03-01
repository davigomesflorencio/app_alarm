package xing.dev.alarm_app.ui.fragments

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import kotlinx.coroutines.launch
import xing.dev.alarm_app.databinding.FragmentAddAlarmBinding
import xing.dev.alarm_app.databinding.WeekdayCardBinding
import xing.dev.alarm_app.domain.database.AlarmDatabase
import xing.dev.alarm_app.ui.viewModels.AddAlarmViewModel
import xing.dev.alarm_app.ui.viewModels.AddAlarmViewModelFactory
import xing.dev.alarm_app.util.hideSoftKeyboard
import xing.dev.alarm_app.util.showBasicMessageDialog


class AddAlarmFragment : Fragment() {

    private lateinit var viewModel: AddAlarmViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddAlarmBinding.inflate(
            layoutInflater, container, false
        )

        val application: Application = requireNotNull(this.activity).application
        val alarmDao = AlarmDatabase.getInstance(application).alarmDao
        val viewModelFactory = AddAlarmViewModelFactory(application, alarmDao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddAlarmViewModel::class.java)


        binding.addAlarmContainer.setOnClickListener {
            hideSoftKeyboard(requireContext(), it)
        }

        binding.saveAlarmButton.setOnClickListener {
            lifecycleScope.launch {
                val saved = viewModel.saveAlarm()
                if (!saved) {
                    showBasicMessageDialog("Não foi possível criar o Alarme!", requireActivity())
                } else {
                    it.findNavController().navigateUp()
                }
            }
        }

        binding.cancelButton.setOnClickListener {
            it.findNavController().navigateUp()
        }


        binding.vibrationSwitch.setOnCheckedChangeListener { p0, p1 ->
            viewModel.addVibration.value = p1
            if (p1) {
                val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        200,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }

        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.hourPicker.value = viewModel.hour.toInt()
        val hourRage = 0..23
        binding.hourPicker.displayedValues = (hourRage.map {
            it.toString().padStart(2, '0')
        }).toTypedArray()
        binding.hourPicker.setOnValueChangedListener { _, _, i ->
            viewModel.hour = i
        }

        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 59
        binding.minutePicker.value = viewModel.minute.toInt()
        val minRange = 0..59
        binding.minutePicker.displayedValues = (minRange.map {
            it.toString().padStart(2, '0')
        }).toTypedArray()
        binding.minutePicker.setOnValueChangedListener { _, _, i ->
            viewModel.minute = i
        }


        val days = arrayListOf("SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM")
        days.forEach {
            val day: String = it
            val weekdayBinding =
                WeekdayCardBinding.inflate(layoutInflater, binding.weekdaysHolder, false)
            weekdayBinding.selected = false
            weekdayBinding.weekDay = it
            val layoutParams = LinearLayout.LayoutParams(
                0,
                (80 * requireContext().resources.displayMetrics.density).toInt(),
                1f
            )
            if (it != "SEG") {
                layoutParams.marginEnd = 10
            }

            weekdayBinding.button.setOnClickListener {
                weekdayBinding.selected = !weekdayBinding.selected!!
                if (weekdayBinding.selected == true) {
                    viewModel.addToSelectedDays(day)

                } else {
                    viewModel.removeFromSelectedDays(day)

                }
            }

            weekdayBinding.root.layoutParams = layoutParams
            binding.weekdaysHolder.addView(weekdayBinding.root)
        }

        return binding.root
    }

}