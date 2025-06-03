package xing.dev.alarm_app.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import xing.dev.alarm_app.core.DatabaseConstants
import xing.dev.alarm_app.data.database.AlarmDatabase
import xing.dev.alarm_app.data.database.MigrationAlarmDb


val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AlarmDatabase::class.java,
            DatabaseConstants.ALARM_DATABASE
        )
            .addMigrations(MigrationAlarmDb.MIGRATION_7_8)
            .build()
    }
    single {
        get<AlarmDatabase>().getAlarmDatabaseDao()
    }
}