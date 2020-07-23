package ru.g0rd1.peoplesfinder.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.g0rd1.peoplesfinder.db.converter.DateConverter
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.db.entity.*

@Database(
    entities = [
        GroupEntity::class,
        GroupDataEntity::class,
        UserEntity::class,
        UserGroupCrossRefEntity::class,
        UserTypeEntity::class,
        UserUserTypeCrossRefEntity::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class)

abstract class Database : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao

    companion object {
        val onCreateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL(deleteUserRelationsTrigger)
                db.execSQL(deleteGroupRelationsTrigger)
            }
        }

        private val deleteUserRelationsTrigger =
            """
            CREATE TRIGGER delete_user_relations
            BEFORE DELETE
            ON ${UserEntity.TABLE_NAME}
            FOR EACH ROW
            BEGIN
            DELETE FROM ${UserGroupCrossRefEntity.TABLE_NAME} WHERE ${UserGroupCrossRefEntity.TABLE_NAME}.${UserGroupCrossRefEntity.Column.USER_ID} = old.${UserEntity.Column.ID};
            END;
            """.trimIndent()

        private val deleteGroupRelationsTrigger =
            """
            CREATE TRIGGER delete_group_relations
            BEFORE DELETE
            ON `${GroupEntity.TABLE_NAME}`
            FOR EACH ROW
            BEGIN
            DELETE FROM ${UserGroupCrossRefEntity.TABLE_NAME} WHERE ${UserGroupCrossRefEntity.TABLE_NAME}.${UserGroupCrossRefEntity.Column.USER_ID} = old.${GroupEntity.Column.ID};
            END;
            """.trimIndent()

    }
}