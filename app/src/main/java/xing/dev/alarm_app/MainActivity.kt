package xing.dev.alarm_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import org.koin.android.ext.android.inject
import xing.dev.alarm_app.domain.vibration.ManagerVibrationAndSound
import xing.dev.alarm_app.presentation.navigation.SetupNavigation
import xing.dev.alarm_app.presentation.theme.AlarmAppTheme
import xing.dev.alarm_app.presentation.viewModels.AddAlarmViewModel
import xing.dev.alarm_app.presentation.viewModels.AlarmsViewModel

class MainActivity : FragmentActivity() {

    private val managerVibrationAndSound: ManagerVibrationAndSound by inject()
    private val addAlarmViewModel: AddAlarmViewModel by inject()
    private val alarmsViewModel: AlarmsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AlarmAppTheme {
                SetupNavigation(addAlarmViewModel, alarmsViewModel)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        managerVibrationAndSound.stopRingTone()
        managerVibrationAndSound.cancelVibrate()
    }
}