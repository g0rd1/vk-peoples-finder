package ru.g0rd1.peoplesfinder.ui.choose.single

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.base.ItemClickListener
import ru.g0rd1.peoplesfinder.databinding.ItemChooseBinding

class SingleChooseItemAdapter<T>(itemClickListener: ItemClickListener<SingleChooseItemViewData<T>>) :
    BindingRecyclerViewAdapter<ItemChooseBinding, SingleChooseItemViewData<T>>(
        R.layout.item_choose,
        SingleChooseItemDiffCallbackFactory<T>(),
        itemClickListener
    ) {

    override fun getSetViewModelToBindingFunction(holderBinding: ItemChooseBinding): (SingleChooseItemViewData<T>) -> Unit =
        holderBinding::setItem
}