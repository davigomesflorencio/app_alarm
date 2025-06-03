package xing.dev.alarm_app.domain.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import xing.dev.alarm_app.core.Constants
import xing.dev.alarm_app.worker.AlarmWorker
import java.util.concurrent.TimeUnit

object WorkerFunctions {

    fun startAlarmWorker(context: Context, action: String) {

        val inputData = workDataOf(
            "action" to action,
        )

        val dataUploadDataWorker: OneTimeWorkRequest =
            OneTimeWorkRequest.Builder(AlarmWorker::class.java)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .setInputData(inputData)
                .addTag(Constants.TAG_ALARM_WORKER)
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            Constants.TAG_ALARM_WORKER,
            ExistingWorkPolicy.APPEND,
            dataUploadDataWorker
        )
    }
}