package xing.dev.alarm_app.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import xing.dev.alarm_app.domain.vibration.ManagerVibrationAndSound


val repositoryModule = module {
    single { ManagerVibrationAndSound(androidContext()) }
}
