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
        GroupDataEntity::class,
        UserEntity::class,
        UserGroupEntity::class,
        UserTypeEntity::class,
        UserUserTypeEntity::class,
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

    companion object {
        val onCreateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL(
                    """
                    CREATE TRIGGER delete_user_relations
                    BEFORE DELETE
                    ON ${UserEntity.TABLE_NAME}
                    FOR EACH ROW
                    BEGIN
                    DELETE FROM ${UserGroupEntity.TABLE_NAME} WHERE ${UserGroupEntity.TABLE_NAME}.${UserGroupEntity.Column.USER_ID} = old.${UserEntity.Column.ID};
                    END;
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TRIGGER delete_group_relations
                    BEFORE DELETE
                    ON `${GroupEntity.TABLE_NAME}`
                    FOR EACH ROW
                    BEGIN
                    DELETE FROM ${UserGroupEntity.TABLE_NAME} WHERE ${UserGroupEntity.TABLE_NAME}.${UserGroupEntity.Column.USER_ID} = old.${GroupEntity.Column.ID};
                    END;
                    """.trimIndent()
                )

                db.execSQL("INSERT INTO `${UserTypeEntity.TABLE_NAME}` (name) VALUES('${UserType.VIEWED.name}')")
                db.execSQL("INSERT INTO `${UserTypeEntity.TABLE_NAME}` (name) VALUES('${UserType.FAVORITE.name}')")
                db.execSQL("INSERT INTO `${UserTypeEntity.TABLE_NAME}` (name) VALUES('${UserType.BLOCKED.name}')")

            }
        }

    }
}