package xing.dev.alarm_app.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import xing.dev.alarm_app.worker.AlarmWorker

val workerModule = module {
    worker { AlarmWorker(androidContext(), get()) }
}
