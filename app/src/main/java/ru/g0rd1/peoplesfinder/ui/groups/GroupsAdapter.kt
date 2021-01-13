package ru.g0rd1.peoplesfinder.ui.groups

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.databinding.ItemGroupBinding
import javax.inject.Inject

class GroupsAdapter @Inject constructor() :
    BindingRecyclerViewAdapter<ItemGroupBinding, GroupViewModel>(
        R.layout.item_group,
        GroupsDiffCallbackFactory()
    ) {

    override fun getSetViewModelToBindingFunction(holderBinding: ItemGroupBinding): (GroupViewModel) -> Unit =
        holderBinding::setVm

}