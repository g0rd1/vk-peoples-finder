package ru.g0rd1.peoplesfinder.ui.results

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.databinding.ItemResultsUserBinding
import ru.g0rd1.peoplesfinder.db.entity.UserEntity
import javax.inject.Inject

class UsersAdapter @Inject constructor() :
    BindingRecyclerViewAdapter<ItemResultsUserBinding, UserEntity>(R.layout.item_results_user) {

    override fun onBindViewHolder(
        holderBinding: ItemResultsUserBinding,
        position: Int,
        items: List<UserEntity>
    ) {
        holderBinding.user = items[position]
    }

}