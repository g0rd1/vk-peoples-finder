package ru.g0rd1.peoplesfinder.ui.groups

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.databinding.ItemGroupBinding

class GroupsAdapter : BindingRecyclerViewAdapter<ItemGroupBinding, GroupViewData>(
        R.layout.item_group,
        GroupsDiffCallbackFactory()
    ) {

    override fun getSetViewModelToBindingFunction(holderBinding: ItemGroupBinding): (GroupViewData) -> Unit =
        holderBinding::setVm

}