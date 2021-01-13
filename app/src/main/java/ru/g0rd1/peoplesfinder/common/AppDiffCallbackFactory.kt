package ru.g0rd1.peoplesfinder.common

import androidx.recyclerview.widget.DiffUtil

interface AppDiffCallbackFactory<T> {

    fun create(oldItems: List<T>, newItems: List<T>): DiffUtil.Callback

}