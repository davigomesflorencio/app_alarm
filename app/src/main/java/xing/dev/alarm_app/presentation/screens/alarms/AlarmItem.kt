package xing.dev.alarm_app.presentation.screens.alarms

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xing.dev.alarm_app.R
import xing.dev.alarm_app.domain.model.Alarm
import xing.dev.alarm_app.presentation.screens.components.DayOfWeekIcon
import xing.dev.alarm_app.presentation.viewModels.AlarmsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmItem(alarmsViewModel: AlarmsViewModel, alarm: Alarm) {
    val scope = rememberCoroutineScope()

    val state = remember { mutableStateOf(false) }

    LaunchedEffect(alarm.disabled) {
        state.value = alarm.disabled
    }

    val deleteActionWidth = 80.dp
    val deleteActionWidthPx = with(LocalDensity.current) { deleteActionWidth.toPx() }

    val swipeableState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(
        0f to 0,
        -deleteActionWidthPx to 1
    )
    val cardElevation by animateDpAsState(
        targetValue = if (swipeableState.isAnimationRunning || swipeableState.offset.value != 0f) 8.dp else 2.dp,
        label = "cardElevation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(.98f)
                .width(deleteActionWidth)
                .background(MaterialTheme.colorScheme.errorContainer)
                .clickable {
                    if (swipeableState.currentValue == 1) {
                        scope.launch {
                            alarmsViewModel.deleteAlarm(alarm)
                            swipeableState.animateTo(0)
                        }
                    } else {
                        scope.launch {
                            swipeableState.animateTo(0)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete Alarm",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(36.dp)
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .fillMaxWidth() // Card should fill width within its constraints
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(vertical = 5.dp)) {
                    Text(
                        text = "Alarme",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_alarm_fill),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = alarm.formattedTime(),
                            fontSize = 40.sp,
                            color = Color.White
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center, // Space between day icons
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        alarmsViewModel.listDays.forEachIndexed { index, initial ->
                            DayOfWeekIcon(
                                dayInitial = initial.substring(0, 1),
                                isSelected = alarm.repeatDays.split(",").contains(initial),
                                modifier = Modifier.padding(horizontal = 1.2.dp)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center
                ) {
                    Switch(
                        state.value,
                        colors = SwitchDefaults.colors(
                            uncheckedIconColor = Color(0XFF333446),
                            uncheckedThumbColor = Color(0XFF333446),
                            checkedTrackColor = Color(0xFF71C0BB),
                            uncheckedTrackColor = Color.White
                        ),
                        onCheckedChange = {
                            if (!it) {
                                alarmsViewModel.disable(alarm)
                            } else {
                                alarmsViewModel.activateAlarm(alarm)
                            }
                        })
//                IconButton(onClick = {
//                    scope.launch {
//                        alarmsViewModel.deleteAlarm(alarm)
//                    }
//                }) {
//                    Icon(
//                        painterResource(R.drawable.ic_baseline_delete_outline_24),
//                        contentDescription = "",
//                        tint = Color(0XFFCD5656)
//                    )
//                }
                }
            }
        }
    }
}
