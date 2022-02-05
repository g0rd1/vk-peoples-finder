package ru.g0rd1.peoplesfinder.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.g0rd1.peoplesfinder.db.converter.DateConverter
import ru.g0rd1.peoplesfinder.db.converter.GroupConverter
import ru.g0rd1.peoplesfinder.db.converter.UserConverter
import ru.g0rd1.peoplesfinder.db.dao.*
import ru.g0rd1.peoplesfinder.db.entity.*
import ru.g0rd1.peoplesfinder.model.UserType

@Database(
    entities = [
        GroupEntity::class,
        GroupInfoEntity::class,
        UserEntity::class,
        UserGroupEntity::class,
        UserTypeEntity::class,
        UserUserTypeEntity::class,
        UserHistoryEntity::class,
    ],
    version = 1
)
@TypeConverters(DateConverter::class, UserConverter::class, GroupConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
    abstract fun groupDataDao(): GroupDataDao
    abstract fun userGroupDao(): UserGroupDao
    abstract fun userTypeDao(): UserTypeDao
    abstract fun userHistoryDao(): UserHistoryDao
    abstract fun userUserTypeDao(): UserUserTypeDao

    companion object {
        val onCreateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL("INSERT INTO `${UserTypeEntity.TABLE_NAME}` (id, name) VALUES(${UserType.FAVORITE.id}, '${UserType.FAVORITE.name}')")
                db.execSQL("INSERT INTO `${UserTypeEntity.TABLE_NAME}` (id, name) VALUES(${UserType.BLOCKED.id}, '${UserType.BLOCKED.name}')")

            }
        }

    }
}