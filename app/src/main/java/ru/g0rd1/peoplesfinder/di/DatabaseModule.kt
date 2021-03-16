package ru.g0rd1.peoplesfinder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.db.Database
import ru.g0rd1.peoplesfinder.db.dao.*
import javax.inject.Singleton

@Module
abstract class DatabaseModule {

    companion object {

        @Provides
        @Singleton
        fun appDatabase(context: Context): Database =
            Room.databaseBuilder(context, Database::class.java, "database")
                .addCallback(Database.onCreateCallback)
                .build()

        @Provides
        @Singleton
        fun userDao(db: Database): UserDao = db.userDao()

        @Provides
        @Singleton
        fun groupDao(db: Database): GroupDao = db.groupDao()

        @Provides
        @Singleton
        fun userGroupDao(db: Database): UserGroupDao = db.userGroupDao()

        @Provides
        @Singleton
        fun groupDataDao(db: Database): GroupDataDao = db.groupDataDao()

        @Provides
        @Singleton
        fun userTypeDao(db: Database): UserTypeDao = db.userTypeDao()

    }

}