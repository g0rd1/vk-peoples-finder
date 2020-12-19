package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverters
import io.reactivex.Completable
import io.reactivex.Single
import ru.g0rd1.peoplesfinder.db.converter.DateConverter
import ru.g0rd1.peoplesfinder.db.entity.GroupDataEntity
import java.util.*

@Dao
@TypeConverters(DateConverter::class)
abstract class GroupDataDao : BaseDao<GroupDataEntity>() {

    @Query("SELECT * FROM `group_data` WHERE groupId = :id")
    abstract fun get(id: Int): Single<List<GroupDataEntity>>

    @Query("SELECT * FROM `group_data`")
    abstract fun get(): Single<List<GroupDataEntity>>

    @Query("UPDATE `group_data` SET loaded_members_count = :loadedMembersCount WHERE groupId = :id")
    abstract fun updateLoadedMembersCount(
        id: Int,
        loadedMembersCount: Int,
    ): Completable

    @Query("UPDATE `group_data` SET all_members_loaded_date = :allMembersLoadedDate WHERE groupId = :id")
    abstract fun updateAllMembersLoadedDate(
        id: Int,
        allMembersLoadedDate: Date?
    ): Completable

    @Query("UPDATE `group_data` SET sequential_number = :sequentialNumber WHERE groupId = :id")
    abstract fun updateSequentialNumber(
        id: Int,
        sequentialNumber: Int
    ): Completable

    @Query("UPDATE `group_data` SET has_access_to_members = :hasAccessToMembers WHERE groupId = :id")
    abstract fun updateHasAccessToMembers(
        id: Int,
        hasAccessToMembers: Boolean
    ): Completable

}