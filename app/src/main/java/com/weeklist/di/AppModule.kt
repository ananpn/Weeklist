package com.weeklist.di

import android.content.Context
import androidx.room.Room
import com.weeklist.database.AppDatabase
import com.weeklist.database.Daos.CountDao
import com.weeklist.database.Daos.GroupDao
import com.weeklist.database.Daos.TaskDao
import com.weeklist.database.OfflineRepository
import com.weeklist.database.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAppDb(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "task_database"
    )
        //.addMigrations(AppDatabase.MIGRATION_12_13)
        .build()

    @Provides
    fun provideTaskDao(
        taskDatabase: AppDatabase
    ) = taskDatabase.taskDao()

    @Provides
    fun provideGroupDao(
        taskDatabase: AppDatabase
    ) = taskDatabase.groupDao()

    @Provides
    fun provideCountDao(
        taskDatabase: AppDatabase
    ) = taskDatabase.countDao()

    @Provides
    fun provideAppRepository(
        taskDao: TaskDao,
        countDao: CountDao,
        groupDao: GroupDao,
    ): TaskRepository = OfflineRepository(
        taskDao = taskDao,
        countDao = countDao,
        groupDao = groupDao
    )

    @Singleton
    @Provides
    fun providePreferenceManager(@ApplicationContext context: Context): PrefsImpl {
        return WeekListPrefs(context)
    }
}
