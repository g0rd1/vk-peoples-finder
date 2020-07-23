package ru.g0rd1.peoplesfinder.ui.groups

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import ru.g0rd1.peoplesfinder.control.groupmembersloader.GroupMembersLoader
import ru.g0rd1.peoplesfinder.db.entity.GroupEntity
import ru.g0rd1.peoplesfinder.model.Group
import java.util.*

data class GroupView(
    val id: Int,
    val name: String,
    val photo: String?,
    private var _membersCount: Int,
    private var _selected: Boolean = false,
    private var _loadedMembersCount: Int = 0,
    private var _allMembersLoadedDate: Date? = null,
    private var _status: GroupMembersLoader.Status = when {
        _allMembersLoadedDate != null -> GroupMembersLoader.Status.FINISH
        _loadedMembersCount == 0 -> GroupMembersLoader.Status.STOPPED
        else -> GroupMembersLoader.Status.PAUSED
    }
) : BaseObservable() {

    var membersCount: Int
        @Bindable get() = _membersCount
        set(value) {
            _membersCount = value
            notifyPropertyChanged(BR.membersCount)
        }

    var selected: Boolean
        @Bindable get() = _selected
        set(value) {
            _selected = value
            notifyPropertyChanged(BR.selected)
        }

    var loadedMembersCount: Int
        @Bindable get() = _loadedMembersCount
        set(value) {
            // Timber.d("group ${this.name} loadedMembersCount changed from $loadedMembersCount to $value")
            _loadedMembersCount = value
            notifyPropertyChanged(BR.loadedMembersCount)
        }

    var allMembersLoadedDate: Date?
        @Bindable get() = _allMembersLoadedDate
        set(value) {
            // Timber.d("group ${this.name} allMembersLoadedDate changed from $allMembersLoadedDate to $value")
            _allMembersLoadedDate = value
            notifyPropertyChanged(BR.allMembersLoadedDate)
        }

    var status: GroupMembersLoader.Status
        @Bindable get() = _status
        set(value) {
            // Timber.d("group ${this.name} status changed from $status to $value")
            _status = value
            notifyPropertyChanged(BR.status)
        }

    constructor(
        group: Group,
        selected: Boolean = false,
        loadedMembersCount: Int = 0
    ) : this(
        id = group.id,
        name = group.name,
        photo = group.photo,
        _membersCount = group.membersCount,
        _selected = selected,
        _loadedMembersCount = loadedMembersCount
    )

    constructor(groupEntity: GroupEntity, selected: Boolean = false) : this(
        id = groupEntity.id,
        name = groupEntity.name,
        photo = groupEntity.photo,
        _membersCount = groupEntity.membersCount,
        _selected = selected,
        _loadedMembersCount = groupEntity.loadedMembersCount,
        _allMembersLoadedDate = groupEntity.allMembersLoadedDate
    )

}