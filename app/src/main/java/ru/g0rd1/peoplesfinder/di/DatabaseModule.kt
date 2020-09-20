package ru.g0rd1.peoplesfinder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.db.Database
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.UserDao
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

    }

}