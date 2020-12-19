package ru.g0rd1.peoplesfinder.ui.results

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.databinding.ItemResultsUserBinding
import javax.inject.Inject

class UsersAdapter @Inject constructor() :
    BindingRecyclerViewAdapter<ItemResultsUserBinding, UserViewModel>(R.layout.item_results_user) {

    override fun getSetViewModelToBindingFunction(holderBinding: ItemResultsUserBinding): (UserViewModel) -> Unit =
        holderBinding::setViewModel

}