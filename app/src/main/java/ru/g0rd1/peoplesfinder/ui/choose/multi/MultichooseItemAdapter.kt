package ru.g0rd1.peoplesfinder.ui.choose.multi

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.base.ItemClickListener
import ru.g0rd1.peoplesfinder.databinding.ItemMultichooseBinding

class MultichooseItemAdapter<T>(itemClickListener: ItemClickListener<MultichooseItemViewModel<T>>) :
    BindingRecyclerViewAdapter<ItemMultichooseBinding, MultichooseItemViewModel<T>>(
        R.layout.item_multichoose,
        MultichooseItemDiffCallbackFactory<T>(),
        itemClickListener
    ) {

    override fun getSetViewModelToBindingFunction(holderBinding: ItemMultichooseBinding): (MultichooseItemViewModel<T>) -> Unit =
        holderBinding::setItem
}