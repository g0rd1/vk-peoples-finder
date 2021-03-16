package ru.g0rd1.peoplesfinder.ui.results

import ru.g0rd1.peoplesfinder.R
import ru.g0rd1.peoplesfinder.base.BindingRecyclerViewAdapter
import ru.g0rd1.peoplesfinder.databinding.ItemResultsUserBinding

class ResultsAdapter : BindingRecyclerViewAdapter<ItemResultsUserBinding, ResultViewModel>(
        R.layout.item_results_user,
        ResultsDiffCallbackFactory()
    ) {

    override fun getSetViewModelToBindingFunction(holderBinding: ItemResultsUserBinding): (ResultViewModel) -> Unit =
        holderBinding::setViewModel

}