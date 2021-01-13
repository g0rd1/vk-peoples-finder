package ru.g0rd1.peoplesfinder.ui.results

import androidx.recyclerview.widget.DiffUtil
import ru.g0rd1.peoplesfinder.common.AppDiffCallbackFactory

class ResultsDiffCallbackFactory : AppDiffCallbackFactory<ResultViewModel> {

    override fun create(
        oldItems: List<ResultViewModel>,
        newItems: List<ResultViewModel>
    ): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].user.id == newItems[newItemPosition].user.id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        }
    }
}