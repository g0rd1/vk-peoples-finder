package ru.g0rd1.peoplesfinder.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.g0rd1.peoplesfinder.db.dao.GroupDao
import ru.g0rd1.peoplesfinder.db.dao.GroupDataDao
import ru.g0rd1.peoplesfinder.db.dao.UserDao
import ru.g0rd1.peoplesfinder.db.dao.UserGroupDao
import ru.g0rd1.peoplesfinder.db.entity.GroupDataEntity
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import ru.g0rd1.peoplesfinder.db.entity.UserGroupEntity

@Database(
    entities = [
        GroupEntity::class,
        GroupDataEntity::class,
        UserEntity::class,
        UserGroupEntity::class
    ],
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
    abstract fun groupDataDao(): GroupDataDao
    abstract fun userGroupDao(): UserGroupDao

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
            DELETE FROM ${UserGroupEntity.TABLE_NAME} WHERE ${UserGroupEntity.TABLE_NAME}.${UserGroupEntity.Column.USER_ID} = old.${UserEntity.Column.ID};
            END;
            """.trimIndent()

        private val deleteGroupRelationsTrigger =
            """
            CREATE TRIGGER delete_group_relations
            BEFORE DELETE
            ON `${GroupEntity.TABLE_NAME}`
            FOR EACH ROW
            BEGIN
            DELETE FROM ${UserGroupEntity.TABLE_NAME} WHERE ${UserGroupEntity.TABLE_NAME}.${UserGroupEntity.Column.USER_ID} = old.${GroupEntity.Column.ID};
            END;
            """.trimIndent()

    }
}