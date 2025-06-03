package xing.dev.alarm_app.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import xing.dev.alarm_app.presentation.screens.addAlarm.AddAlarm
import xing.dev.alarm_app.presentation.screens.alarms.Alarms
import xing.dev.alarm_app.presentation.viewModels.AddAlarmViewModel
import xing.dev.alarm_app.presentation.viewModels.AlarmsViewModel

@Composable
fun SetupNavigation(addAlarmViewModel: AddAlarmViewModel, alarmsViewModel: AlarmsViewModel) {
    val backStack = remember { mutableStateListOf<Any>(Alarms) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = {
                // Slide in from right when navigating forward
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                // Slide in from left when navigating back
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                // Slide in from left when navigating back
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            entryProvider = { key ->
                when (key) {
                    is Alarms -> NavEntry(key) {
                        Alarms(alarmsViewModel, backStack)
                    }

                    is AddAlarm -> NavEntry(key) {
                        AddAlarm(addAlarmViewModel, backStack)
                    }

                    is DetailsAlarm -> NavEntry(key) {
//                        DetailsAlarmScreen(addAlarmViewModel, backStack)
                    }

                    is EditAlarm -> NavEntry(key) {
//                        EditAlarm(addAlarmViewModel, backStack, key.alarm)
                    }

                    is EditDetailsAlarm -> NavEntry(key) {
//                        EditDetailsAlarm(addAlarmViewModel, backStack, key.alarm)
                    }

                    is AlarmDaysWeekScreen -> NavEntry(key) {
//                        AlarmDaysWeekScreen(addAlarmViewModel, backStack)
                    }

                    else -> {
                        error("Unknown route: $key")
                    }
                }
            })
    }
}
