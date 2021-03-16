package ru.g0rd1.peoplesfinder.ui.userDetail

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.databinding.ItemUserDetailSameGroupBinding
import ru.g0rd1.peoplesfinder.model.Group
import javax.inject.Inject

class SameGroupsAdapter @Inject constructor() :
    BindingRecyclerViewAdapter<ItemUserDetailSameGroupBinding, Group>(
        R.layout.item_user_detail_same_group,
        SameGroupCallbackFactory()
    ) {

    override fun getSetViewModelToBindingFunction(holderBinding: ItemUserDetailSameGroupBinding): (Group) -> Unit =
        holderBinding::setGroup

}