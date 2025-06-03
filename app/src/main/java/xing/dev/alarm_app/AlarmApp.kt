package xing.dev.alarm_app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import xing.dev.alarm_app.di.injectMobileFeature

class AlarmApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AlarmApp)
            injectMobileFeature()
        }

        val myConfig = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

        WorkManager.initialize(this, myConfig)
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}