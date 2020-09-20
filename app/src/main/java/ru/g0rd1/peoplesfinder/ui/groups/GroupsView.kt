package ru.g0rd1.peoplesfinder.ui.groups

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

data class GroupsView(
    private var _startOrPauseButtonText: String = "",
    private var _showStopButton: Boolean = false
) : BaseObservable() {

    var startOrPauseButtonText: String
        @Bindable get() = _startOrPauseButtonText
        set(value) {
            _startOrPauseButtonText = value
            notifyPropertyChanged(BR.startOrPauseButtonText)
        }

    var showStopButton: Boolean
        @Bindable get() = _showStopButton
        set(value) {
            _showStopButton = value
            notifyPropertyChanged(BR.showStopButton)
        }

}