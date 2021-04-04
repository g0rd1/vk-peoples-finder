package ru.g0rd1.peoplesfinder.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import ru.g0rd1.peoplesfinder.db.entity.GroupDataEntity
import java.time.LocalDate

@Dao
abstract class GroupDataDao : BaseDao<GroupDataEntity>() {

    @Query("UPDATE `group_data` SET loaded_members_count = :loadedMembersCount WHERE groupId = :id")
    abstract fun updateLoadedMembersCount(
        id: Int,
        loadedMembersCount: Int,
    ): Completable

    @Query("UPDATE `group_data` SET all_members_loaded_date = :allMembersLoadedDate WHERE groupId = :id")
    abstract fun updateAllMembersLoadedDate(
        id: Int,
        allMembersLoadedDate: LocalDate?,
    ): Completable

    @Query("UPDATE `group_data` SET sequential_number = :sequentialNumber WHERE groupId = :id")
    abstract fun updateSequentialNumber(
        id: Int,
        sequentialNumber: Int,
    ): Completable

    @Query("UPDATE `group_data` SET has_access_to_members = :hasAccessToMembers WHERE groupId = :id")
    abstract fun updateHasAccessToMembers(
        id: Int,
        hasAccessToMembers: Boolean,
    ): Completable

}