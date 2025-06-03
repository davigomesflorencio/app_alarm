package xing.dev.alarm_app.presentation.screens.addAlarm

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import kotlinx.coroutines.launch
import xing.dev.alarm_app.presentation.viewModels.AddAlarmViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddAlarm(
    addAlarmViewModel: AddAlarmViewModel,
    backStack: SnapshotStateList<Any>
) {
    val scope = rememberCoroutineScope()
    val listDays = addAlarmViewModel.listDays
    val calendar = Calendar.getInstance()

    var pickerValue by remember {
        mutableStateOf<Hours>(
            FullHours(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        )
    }
    var soundActive by remember { mutableStateOf(false) }
    var alarmVibrate by remember { mutableStateOf(false) }
    var snoozeActive by remember { mutableStateOf(false) }
    val listActiveDays = remember { mutableStateListOf(*List(7) { false }.toTypedArray()) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    contentColor = Color.White,
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HoursNumberPicker(
                        dividersColor = Color.Transparent,
                        leadingZero = true,
                        value = pickerValue,
                        onValueChange = {
                            pickerValue = it
                        },
                        hoursDivider = {
                            Text(
                                modifier = Modifier
                                    .height(34.dp)
                                    .width(5.dp),
                                textAlign = TextAlign.Center,
                                text = ":",
                                fontSize = 30.sp,
                                color = Color.Black
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 40.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth(.6f)
                            .height(250.dp)
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(
                    contentColor = Color.White,
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp, horizontal = 10.dp)
                ) {
                    Text(
                        if (addAlarmViewModel.days.isEmpty) {
                            "Amanhã"
                        } else {
                            "A cada " + addAlarmViewModel.listDays.filter {
                                addAlarmViewModel.days.contains(it)
                            }.joinToString(",") { x -> x }
                        },
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryFixed
                        ))
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 5.dp)
                ) {
                    listDays.forEachIndexed { index, item ->
                        ToggleButton(
                            checked = listActiveDays[index],
                            onCheckedChange = {
                                listActiveDays[index] = !listActiveDays[index]
                                if (listActiveDays[index]) {
                                    addAlarmViewModel.addToSelectedDays(listDays[index])
                                } else {
                                    addAlarmViewModel.removeFromSelectedDays(listDays[index])
                                }
                            },
                            content = {
                                Text(
                                    item.substring(0, 1),
                                    color = Color.White
                                )
                            }
                        )
                    }
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                ) {
                    Text(
                        "Som",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryFixed
                        )
                    )
                    Switch(
                        soundActive,
                        colors = SwitchDefaults.colors(
                            uncheckedIconColor = Color(0XFF333446),
                            uncheckedThumbColor = Color(0XFF333446),
                            checkedTrackColor = Color(0xFF71C0BB),
                            uncheckedTrackColor = Color.White
                        ),
                        onCheckedChange = {
                            soundActive = it
                            addAlarmViewModel.addSound.value = soundActive
                        })
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                ) {
                    Text(
                        "Vibração",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryFixed
                        )
                    )
                    Switch(
                        alarmVibrate,
                        colors = SwitchDefaults.colors(
                            uncheckedIconColor = Color(0XFF333446),
                            uncheckedThumbColor = Color(0XFF333446),
                            checkedTrackColor = Color(0xFF71C0BB),
                            uncheckedTrackColor = Color.White
                        ),
                        onCheckedChange = {
                            alarmVibrate = it
                            addAlarmViewModel.addVibration.value = alarmVibrate
                        })
                }
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                ) {
                    Text(
                        "Soneca",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryFixed
                        )
                    )
                    Switch(
                        snoozeActive,
                        colors = SwitchDefaults.colors(
                            uncheckedIconColor = Color(0XFF333446),
                            uncheckedThumbColor = Color(0XFF333446),
                            checkedTrackColor = Color(0xFF71C0BB),
                            uncheckedTrackColor = Color.White
                        ),
                        onCheckedChange = {
                            snoozeActive = it
                            addAlarmViewModel.addSnooze.value = snoozeActive
                        })
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    backStack.removeLastOrNull()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0XFFCD5656),
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                modifier = Modifier.weight(.4f)
            ) {
                Text("Cancelar", style = TextStyle(fontSize = 20.sp))
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(
                onClick = {
                    scope.launch {
                        addAlarmViewModel.hour.intValue = pickerValue.hours
                        addAlarmViewModel.minute.intValue = pickerValue.minutes
                        addAlarmViewModel.saveAlarm()
                    }
                    backStack.removeLastOrNull()
                },
                elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
                modifier = Modifier.weight(.4f)
            ) {
                Text("Salvar", style = TextStyle(fontSize = 20.sp))
            }
        }
    }
}