package ru.g0rd1.peoplesfinder.ui.choose.multi

import androidx.recyclerview.widget.DiffUtil
import ru.g0rd1.peoplesfinder.common.AppDiffCallbackFactory

class MultichooseItemDiffCallbackFactory <T> : AppDiffCallbackFactory<MultichooseItemViewData<T>> {

    override fun create(
        oldItems: List<MultichooseItemViewData<T>>,
        newItems: List<MultichooseItemViewData<T>>
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