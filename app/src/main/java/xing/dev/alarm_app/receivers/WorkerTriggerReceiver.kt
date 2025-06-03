package xing.dev.alarm_app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import xing.dev.alarm_app.domain.worker.WorkerFunctions

class WorkerTriggerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        WorkerFunctions.startAlarmWorker(context, action ?: "")
    }
}