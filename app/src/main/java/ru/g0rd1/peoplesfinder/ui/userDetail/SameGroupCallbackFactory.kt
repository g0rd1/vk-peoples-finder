package ru.g0rd1.peoplesfinder.ui.userDetail

import androidx.recyclerview.widget.DiffUtil
import ru.g0rd1.peoplesfinder.common.AppDiffCallbackFactory
import ru.g0rd1.peoplesfinder.model.Group

class SameGroupCallbackFactory : AppDiffCallbackFactory<Group> {

    override fun create(
        oldItems: List<Group>,
        newItems: List<Group>
    ): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        }
    }
}