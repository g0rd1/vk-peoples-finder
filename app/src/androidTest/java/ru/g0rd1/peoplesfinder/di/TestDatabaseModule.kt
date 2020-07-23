package ru.g0rd1.peoplesfinder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.g0rd1.peoplesfinder.db.Database
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import javax.inject.Named
import javax.inject.Singleton

@Module
abstract class TestDatabaseModule : DatabaseModule() {

    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun appDatabase(context: Context): Database =
            Room.inMemoryDatabaseBuilder(context, Database::class.java)
                .addCallback(Database.onCreateCallback)
                .build()

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun userDao(@Named("TEST") db: Database): UserDao = db.userDao()

        @JvmStatic
        @Provides
        @Singleton
        @Named("TEST")
        fun groupDao(@Named("TEST") db: Database): GroupDao = db.groupDao()
    }
}