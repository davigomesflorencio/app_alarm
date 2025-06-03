package xing.dev.alarm_app.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import xing.dev.alarm_app.core.DatabaseConstants

object MigrationAlarmDb {

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE ${DatabaseConstants.TABLE_ALARM} ADD COLUMN snoozeActive INTEGER NOT NULL DEFAULT 0")
        }
    }
}