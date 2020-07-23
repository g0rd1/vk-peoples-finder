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

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun appDatabase(context: Context): Database =
            Room.databaseBuilder(context, Database::class.java, "database")
                .addCallback(Database.onCreateCallback)
                .build()

        @JvmStatic
        @Provides
        @Singleton
        fun userDao(db: Database): UserDao = db.userDao()

        @JvmStatic
        @Provides
        @Singleton
        fun groupDao(db: Database): GroupDao = db.groupDao()

    }

}