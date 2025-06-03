package xing.dev.alarm_app.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import xing.dev.alarm_app.presentation.viewModels.AddAlarmViewModel
import xing.dev.alarm_app.presentation.viewModels.AlarmsViewModel

val viewModelModule = module {
    viewModel { AddAlarmViewModel(androidApplication(), get()) }
    viewModel { AlarmsViewModel(androidApplication(), get()) }
}