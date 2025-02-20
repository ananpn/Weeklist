package com.weeklist.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.weeklist.LogicAndData.Constants.Companion.GROUP_TABLE
import com.weeklist.database.Daos.CountDao
import com.weeklist.database.Daos.GroupDao
import com.weeklist.database.Daos.TaskDao

@Database(entities = [Task::class, TaskCount::class, TaskGroup::class],
    version = 14,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 13, to = 14)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun groupDao(): GroupDao
    abstract fun countDao(): CountDao
    companion object {
        val MIGRATION_12_13 = migration_12_13
        val MIGRATION_13_14 = migration_13_14
    }
}

val migration_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Perform the necessary database migration operations here
        database.execSQL("ALTER TABLE $GROUP_TABLE ADD COLUMN defaultClickStep INTEGER NOT NULL DEFAULT 1")
    }
}

val migration_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}





