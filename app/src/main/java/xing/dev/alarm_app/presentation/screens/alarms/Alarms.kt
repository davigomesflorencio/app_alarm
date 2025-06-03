package xing.dev.alarm_app.presentation.screens.alarms

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import xing.dev.alarm_app.presentation.navigation.AddAlarm
import xing.dev.alarm_app.presentation.viewModels.AlarmsViewModel


@Composable
fun Alarms(
    alarmsViewModel: AlarmsViewModel,
    backStack: SnapshotStateList<Any>
) {

    val items = remember { alarmsViewModel.alarms }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->

        }
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // State to track permission status
    var hasPermission by remember { mutableStateOf(alarmManager.canScheduleExactAlarms()) }

    // Listen for permission changes
    val permissionListener = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                hasPermission = alarmManager.canScheduleExactAlarms()
            }
        }
    }

    // Register receiver
    DisposableEffect(Unit) {
        context.registerReceiver(
            permissionListener,
            IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)
        )
        onDispose {
            context.unregisterReceiver(permissionListener)
        }
    }

    LaunchedEffect(Unit) { // Simple trigger on composition

        val activity = context as? Activity // Or obtain activity differently
        if (activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) == false) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    backStack.add(AddAlarm)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = ""
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 10.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            itemsIndexed(items.value) { index, item ->
                AlarmItem(alarmsViewModel, item)
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}
